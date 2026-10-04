package org.openldes.server;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.openldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.openldes.server.domain.constants.RdfConstants.TREE_NODE;
import static org.openldes.server.fragmentation.batch.FragmentationJobDefinitions.FRAGMENTATION_JOB;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.openldes.server.resultactionsextensions.ResponseToModelConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Steps that reproduce <a href="https://github.com/OpenLDES/LDESServer/issues/48">issue 48</a>: they ingest members
 * into a collection with several views and afterwards verify that each of those views actually contains every member.
 */
public class MissingMembersSteps extends LdesServerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(MissingMembersSteps.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final LocalDateTime FIRST_MEMBER_TIMESTAMP = LocalDateTime.of(2024, 1, 1, 0, 0);
    /**
     * Spreading the members over several days makes sure that the time based view really has to bucketise them into
     * more than one bucket, just like it has to in the setup described in the issue.
     */
    private static final int MINUTES_BETWEEN_MEMBERS = 17;
    private static final String MEMBER_COUNT_PER_VIEW = """
            SELECT v.name AS view_name, COUNT(DISTINCT pm.member_id) AS member_count
            FROM collections c
                     JOIN views v ON v.collection_id = c.collection_id
                     LEFT JOIN page_members pm ON pm.view_id = v.view_id AND pm.page_id IS NOT NULL
            WHERE c.name = ?
            GROUP BY v.name
            ORDER BY v.name
            """;
    /**
     * The number of consecutive idle polls before fragmentation is considered finished. A single idle poll is not
     * enough: the scheduler launches the job of every view asynchronously, so there is a short window in which no job
     * is running yet while there is still work queued.
     */
    private static final int REQUIRED_CONSECUTIVE_IDLE_POLLS = 5;

    @When("I concurrently ingest {int} members of template {string} to the collection {string} using {int} threads")
    public void iConcurrentlyIngestMembers(int numberOfMembers, String memberTemplate, String collection,
                                           int numberOfThreads) throws Exception {
        log.atDebug().log("When I concurrently ingest {} members of template {} to the collection {} using {} threads",
                numberOfMembers, memberTemplate, collection, numberOfThreads);
        final String memberContentTemplate = readMemberTemplate(memberTemplate);
        final List<Exception> failures = Collections.synchronizedList(new ArrayList<>());
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        try {
            for (int i = 0; i < numberOfMembers; i++) {
                final int memberIndex = i;
                executorService.execute(() -> {
                    try {
                        ingestMember(memberContentTemplate, memberIndex, collection);
                    } catch (Exception e) {
                        failures.add(e);
                    }
                });
            }
        } finally {
            executorService.shutdown();
            assertThat(executorService.awaitTermination(5, MINUTES))
                    .as("all %d members were ingested in time".formatted(numberOfMembers))
                    .isTrue();
        }

        assertThat(failures).as("failures while concurrently ingesting members").isEmpty();
    }

    private void ingestMember(String memberContentTemplate, int memberIndex, String collection) throws Exception {
        final String memberContent = memberContentTemplate
                .replace("ID", String.valueOf(memberIndex))
                .replace("DATETIME", FIRST_MEMBER_TIMESTAMP
                        .plusMinutes((long) memberIndex * MINUTES_BETWEEN_MEMBERS)
                        .format(DATE_TIME_FORMATTER));
        mockMvc.perform(post("/" + collection)
                        .contentType("text/turtle")
                        .content(memberContent))
                .andExpect(status().is2xxSuccessful());
    }

    private String readMemberTemplate(String fileName) throws IOException, URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(path).collect(Collectors.joining());
    }

    @And("fragmentation of {string} has settled")
    public void fragmentationHasSettled(String collection) {
        log.atDebug().log("And fragmentation of {} has settled", collection);
        final AtomicInteger consecutiveIdlePolls = new AtomicInteger();
        await().atMost(180, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    if (isFragmentationIdle()) {
                        return consecutiveIdlePolls.incrementAndGet() >= REQUIRED_CONSECUTIVE_IDLE_POLLS;
                    }
                    consecutiveIdlePolls.set(0);
                    return false;
                });
        log.atDebug().log("Fragmentation of {} settled with member counts {}", collection, getMemberCountPerView(collection));
    }

    private boolean isFragmentationIdle() {
        return unprocessedViewRepository.findAll().isEmpty()
                && jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB).isEmpty();
    }

    @And("every view of {string} contains {int} members")
    public void everyViewContainsMembers(String collection, int expectedMemberCount) {
        log.atDebug().log("And every view of {} contains {} members", collection, expectedMemberCount);
        final Map<String, Long> memberCountPerView = getMemberCountPerView(collection);

        assertThat(memberCountPerView)
                .as("members that were paginated into each view of %s: %s", collection, memberCountPerView)
                .isNotEmpty()
                .allSatisfy((viewName, memberCount) -> assertThat(memberCount)
                        .as("members in view %s", viewName)
                        .isEqualTo(expectedMemberCount));
    }

    private Map<String, Long> getMemberCountPerView(String collection) {
        final Map<String, Long> memberCountPerView = new LinkedHashMap<>();
        jdbcTemplate.queryForList(MEMBER_COUNT_PER_VIEW, collection)
                .forEach(row -> memberCountPerView.put((String) row.get("view_name"),
                        ((Number) row.get("member_count")).longValue()));
        return memberCountPerView;
    }

    @And("traversing the {string} view of {string} yields {int} distinct members")
    public void traversingTheViewYieldsDistinctMembers(String view, String collection, int expectedMemberCount)
            throws Exception {
        log.atDebug().log("And traversing the {} view of {} yields {} distinct members", view, collection, expectedMemberCount);
        final Set<String> members = collectMembersByTraversingTheView("/%s/%s".formatted(collection, view));

        assertThat(members)
                .as("distinct members that are reachable by traversing the %s view of %s", view, collection)
                .hasSize(expectedMemberCount);
    }

    private Set<String> collectMembersByTraversingTheView(String rootFragment) throws Exception {
        final Set<String> visitedFragments = new HashSet<>();
        final Set<String> members = new HashSet<>();
        final Deque<String> fragmentsToVisit = new ArrayDeque<>();
        fragmentsToVisit.add(rootFragment);

        while (!fragmentsToVisit.isEmpty()) {
            final String fragment = fragmentsToVisit.poll();
            if (!visitedFragments.add(fragment)) {
                continue;
            }
            final Model fragmentModel = fetchFragment(fragment);
            fragmentModel.listObjectsOfProperty(TREE_MEMBER)
                    .forEachRemaining(member -> members.add(member.toString()));
            fragmentModel.listObjectsOfProperty(TREE_NODE)
                    .forEachRemaining(node -> fragmentsToVisit.add(node.toString()));
        }

        log.atDebug().log("Traversed {} fragments starting from {} and found {} members",
                visitedFragments.size(), rootFragment, members.size());
        return members;
    }

    private Model fetchFragment(String path) throws Exception {
        final MockHttpServletResponse response = mockMvc.perform(get(new URI(path)).accept("text/turtle"))
                .andReturn()
                .getResponse();
        if (response.getStatus() == 404) {
            return ModelFactory.createDefaultModel();
        }
        return new ResponseToModelConverter(response).convert();
    }
}
