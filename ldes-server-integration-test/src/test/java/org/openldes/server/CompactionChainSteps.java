package org.openldes.server;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Steps that reproduce <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a>: they bring a view into
 * the shape it has after a version based retention policy has run - pages that are below capacity but still more than
 * half full - and afterwards verify that the chain of pages that compaction leaves behind is still a chain that reaches
 * every page from the root.
 */
public class CompactionChainSteps extends LdesServerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(CompactionChainSteps.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    /**
     * Every page of the view that still holds members but that cannot be reached by following the relations from the
     * root page. Pages that were absorbed by a compacted page are skipped: they have an expiration and are deleted
     * shortly afterwards.
     */
    private static final String UNREACHABLE_PAGES_WITH_MEMBERS = """
            WITH RECURSIVE reachable_pages(page_id) AS (SELECT p.page_id
                                                        FROM pages p
                                                                 JOIN buckets b ON b.bucket_id = p.bucket_id
                                                                 JOIN views v ON v.view_id = b.view_id
                                                                 JOIN collections c ON c.collection_id = v.collection_id
                                                        WHERE c.name = ?
                                                          AND v.name = ?
                                                          AND p.is_root
                                                        UNION
                                                        SELECT r.to_page_id
                                                        FROM page_relations r
                                                                 JOIN reachable_pages rp ON rp.page_id = r.from_page_id)
            SELECT p.page_id, p.partial_url, COUNT(pm.member_id) AS member_count
            FROM pages p
                     JOIN buckets b ON b.bucket_id = p.bucket_id
                     JOIN views v ON v.view_id = b.view_id
                     JOIN collections c ON c.collection_id = v.collection_id
                     JOIN page_members pm ON pm.page_id = p.page_id
            WHERE c.name = ?
              AND v.name = ?
              AND p.expiration IS NULL
              AND p.page_id NOT IN (SELECT page_id FROM reachable_pages)
            GROUP BY p.page_id, p.partial_url
            ORDER BY p.page_id
            """;
    private static final String RELATIONS_OF_VIEW = """
            SELECT r.from_page_id, r.to_page_id
            FROM page_relations r
                     JOIN pages p ON p.page_id = r.from_page_id
                     JOIN buckets b ON b.bucket_id = p.bucket_id
                     JOIN views v ON v.view_id = b.view_id
                     JOIN collections c ON c.collection_id = v.collection_id
            WHERE c.name = ?
              AND v.name = ?
            ORDER BY r.from_page_id, r.to_page_id
            """;
    private static final String ROOT_PAGE_OF_VIEW = """
            SELECT p.page_id
            FROM pages p
                     JOIN buckets b ON b.bucket_id = p.bucket_id
                     JOIN views v ON v.view_id = b.view_id
                     JOIN collections c ON c.collection_id = v.collection_id
            WHERE c.name = ?
              AND v.name = ?
              AND p.is_root
            """;
    private static final String MEMBER_COUNT_OF_VIEW = """
            SELECT COUNT(DISTINCT pm.member_id)
            FROM collections c
                     JOIN views v ON v.collection_id = c.collection_id
                     JOIN page_members pm ON pm.view_id = v.view_id AND pm.page_id IS NOT NULL
            WHERE c.name = ?
              AND v.name = ?
            """;

    /**
     * Ingests one version of each of the given state objects. Ingesting the same state object a second time adds a
     * newer version of it, which is what makes the retention policy drop the version that is already on a page.
     *
     * @param stateObjectIds comma separated list of identifiers, where each item is either a single number or an
     *                       inclusive range such as {@code 0-15}
     */
    @When("I ingest a version of the state objects {string} of template {string} to the collection {string}")
    public void iIngestAVersionOfTheStateObjects(String stateObjectIds, String template, String collection)
            throws Exception {
        log.atDebug().log("When I ingest a version of the state objects {} of template {} to the collection {}",
                stateObjectIds, template, collection);
        final String memberTemplate = readMemberTemplate(template);

        for (Integer stateObjectId : parseStateObjectIds(stateObjectIds)) {
            final String memberContent = memberTemplate
                    .replace("ID", String.valueOf(stateObjectId))
                    .replace("TIMESTAMP", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
            mockMvc.perform(post("/" + collection)
                            .contentType("text/turtle")
                            .content(memberContent))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @Then("retention leaves the {string} view of {string} with {int} members")
    public void retentionLeavesTheViewWithMembers(String view, String collection, int expectedMemberCount) {
        log.atDebug().log("Then retention leaves the {} view of {} with {} members", view, collection,
                expectedMemberCount);
        await().atMost(120, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> assertThat(getMemberCountOfView(collection, view))
                        .as("members that are still on a page of the %s view of %s", view, collection)
                        .isEqualTo(expectedMemberCount));
    }

    @Then("every page of the {string} view of {string} that holds members is reachable from its root page")
    public void everyPageThatHoldsMembersIsReachableFromItsRootPage(String view, String collection) {
        log.atDebug().log("Then every page of the {} view of {} that holds members is reachable from its root page",
                view, collection);
        final List<Map<String, Object>> unreachablePages =
                jdbcTemplate.queryForList(UNREACHABLE_PAGES_WITH_MEMBERS, collection, view, collection, view);

        assertThat(unreachablePages)
                .as("pages of the %s view of %s that still hold members but that no client can reach. %s",
                        view, collection, describeChain(collection, view))
                .isEmpty();
    }

    /**
     * A view is a chain of pages, so following the relations from the root page may never arrive at a page that was
     * already visited.
     */
    @And("the page chain of the {string} view of {string} does not loop back on itself")
    public void thePageChainDoesNotLoopBackOnItself(String view, String collection) {
        log.atDebug().log("And the page chain of the {} view of {} does not loop back on itself", view, collection);
        assertThat(pageThatIsVisitedTwice(collection, view))
                .as("the page of the %s view of %s that is reached a second time while following the relations from "
                        + "the root page. %s", view, collection, describeChain(collection, view))
                .isEmpty();
    }

    private Optional<Long> pageThatIsVisitedTwice(String collection, String view) {
        final Map<Long, List<Long>> successorsPerPage = getSuccessorsPerPage(collection, view);
        final Set<Long> visitedPages = new HashSet<>();
        final Deque<Long> pagesToVisit = new ArrayDeque<>(getRootPageIds(collection, view));

        while (!pagesToVisit.isEmpty()) {
            final Long pageId = pagesToVisit.pop();
            if (!visitedPages.add(pageId)) {
                return Optional.of(pageId);
            }
            successorsPerPage.getOrDefault(pageId, List.of()).forEach(pagesToVisit::push);
        }
        return Optional.empty();
    }

    private Map<Long, List<Long>> getSuccessorsPerPage(String collection, String view) {
        return getRelations(collection, view).stream()
                .collect(Collectors.groupingBy(relation -> relation[0],
                        Collectors.mapping(relation -> relation[1], Collectors.toList())));
    }

    private List<Long[]> getRelations(String collection, String view) {
        return jdbcTemplate.queryForList(RELATIONS_OF_VIEW, collection, view).stream()
                .map(row -> new Long[] {((Number) row.get("from_page_id")).longValue(),
                        ((Number) row.get("to_page_id")).longValue()})
                .toList();
    }

    private List<Long> getRootPageIds(String collection, String view) {
        return jdbcTemplate.queryForList(ROOT_PAGE_OF_VIEW, Long.class, collection, view);
    }

    private long getMemberCountOfView(String collection, String view) {
        return Objects.requireNonNullElse(
                jdbcTemplate.queryForObject(MEMBER_COUNT_OF_VIEW, Long.class, collection, view), 0L);
    }

    private String describeChain(String collection, String view) {
        final String relations = getRelations(collection, view).stream()
                .map(relation -> "%d -> %d".formatted(relation[0], relation[1]))
                .toList()
                .toString();
        return "Root pages: %s, relations: %s".formatted(getRootPageIds(collection, view), relations);
    }

    private static List<Integer> parseStateObjectIds(String stateObjectIds) {
        return Arrays.stream(stateObjectIds.split(","))
                .map(String::trim)
                .flatMap(CompactionChainSteps::expandRange)
                .toList();
    }

    private static Stream<Integer> expandRange(String stateObjectIdOrRange) {
        final int separatorIndex = stateObjectIdOrRange.indexOf('-');
        if (separatorIndex < 0) {
            return Stream.of(Integer.parseInt(stateObjectIdOrRange));
        }
        return IntStream.rangeClosed(Integer.parseInt(stateObjectIdOrRange.substring(0, separatorIndex)),
                        Integer.parseInt(stateObjectIdOrRange.substring(separatorIndex + 1)))
                .boxed();
    }

    private String readMemberTemplate(String fileName) throws IOException, URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(path).collect(Collectors.joining(System.lineSeparator()));
    }
}
