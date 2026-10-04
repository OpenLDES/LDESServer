package org.openldes.server.compaction.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import io.micrometer.observation.ObservationRegistry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.compaction.application.services.CompactionCandidateSorter;
import org.openldes.server.compaction.application.services.PageDeletionTimeSetter;
import org.openldes.server.compaction.domain.entities.CompactedFragmentCreator;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;
import org.openldes.server.compaction.domain.repository.CompactionPageRelationRepository;
import org.openldes.server.domain.exceptions.PageListSortException;
import org.openldes.server.maintenance.repository.PageMemberRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Regression test for <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a> at the level of a
 * complete compaction run: it replays what {@link CompactionTask} does - sort the candidates, then write every
 * compacted page - against an in memory copy of the {@code pages}, {@code page_members} and {@code page_relations}
 * tables, and afterwards walks the resulting chain the way a client does.
 * <p>
 * The starting point is the shape a view has after a version based retention policy has run: the pages are below
 * capacity but each of them is still more than half full, so no two neighbours fit into a single compacted page.
 */
class CompactionChainIntegrityTest {
	/** The {@code tree:pageSize} of the view: a page holds fewer members than this to be a compaction candidate. */
	private static final int CAPACITY_PER_PAGE = 8;
	private static final int QUARTER_OF_A_PAGE = CAPACITY_PER_PAGE / 4;
	/** What a version based retention policy left on every page: below capacity, but still three quarters full. */
	private static final int MEMBERS_PER_PAGE = 3 * QUARTER_OF_A_PAGE;
	private static final int NUMBER_OF_IMMUTABLE_PAGES = 6;
	private static final long ROOT_PAGE_ID = 100L;
	private static final long BUCKET_ID = 1L;

	private PageChain pageChain;
	private CompactionWriter compactionWriter;

	@BeforeEach
	void setUp() {
		pageChain = PageChain.ofPagesWithEqualSize(NUMBER_OF_IMMUTABLE_PAGES, MEMBERS_PER_PAGE);
		compactionWriter = compactionWriterFor(pageChain);
	}

	@Test
	void given_PagesThatDoNotFitPairwise_when_Compacting_then_EveryPageWithMembersStaysReachableFromTheRoot() {
		compact();

		assertThat(pageChain.pagesWithMembersThatAreUnreachableFromTheRoot())
				.as("pages that still hold members but that are no longer reachable from the root page. %s",
						pageChain.describe())
				.isEmpty();
	}

	@Test
	void given_PagesThatDoNotFitPairwise_when_Compacting_then_NoMembersBecomeUnreachable() {
		final long memberCountBeforeCompaction = pageChain.totalMemberCount();

		compact();

		assertThat(pageChain.reachableMemberCount())
				.as("members that are found by following the relations from the root page. %s", pageChain.describe())
				.isEqualTo(memberCountBeforeCompaction);
	}

	/**
	 * Writing a group of pages that are not adjacent would rewire the relations into a cycle: page 2 is rewritten to
	 * point at the compacted page because its successor was absorbed, while the compacted page keeps pointing at page 2
	 * because page 1 was taken as the last page of the group. The writer therefore refuses such a group and leaves the
	 * chain untouched.
	 */
	@Test
	void given_ANonAdjacentGroupOfPages_when_Writing_then_TheGroupIsRefusedAndTheChainIsLeftUntouched() {
		final List<CompactionCandidate> candidates = pageChain.getCompactionCandidates(CAPACITY_PER_PAGE);
		final Set<CompactionCandidate> nonAdjacentPages =
				new LinkedHashSet<>(List.of(candidates.get(0), candidates.get(2)));
		final String chainBeforeTheWrite = pageChain.describe();

		assertThatThrownBy(() -> compactionWriter.write(nonAdjacentPages))
				.as("pages 1 and 3 do not follow each other in the chain, so they may not become one page")
				.isInstanceOf(PageListSortException.class);

		assertThat(pageChain.describe())
				.as("a refused group may not have changed the pages, the members or the relations")
				.isEqualTo(chainBeforeTheWrite);
		assertThat(pageChain.pageThatIsVisitedTwiceWhenWalkingFromTheRoot())
				.as("the page that is reached a second time while walking the chain from the root. %s",
						pageChain.describe())
				.isEmpty();
	}

	/**
	 * The example from the <a href="https://openldes.github.io/LDESServer/4.1.4/features/compaction">compaction
	 * documentation</a>: fragments 1 and 2 are full and are therefore not underutilised, fragments 3, 4 and 5 each
	 * hold a quarter of a page and are merged into the single fragment "3/5" that holds three quarters of a page and
	 * that takes their place between fragment 2 and the open fragment 6.
	 */
	@Test
	void given_TheExampleFromTheDocumentation_when_Compacting_then_TheRunOfSmallFragmentsBecomesOneFragment() {
		pageChain = PageChain.ofPagesWithSizes(
				List.of(CAPACITY_PER_PAGE, CAPACITY_PER_PAGE, QUARTER_OF_A_PAGE, QUARTER_OF_A_PAGE, QUARTER_OF_A_PAGE),
				3 * QUARTER_OF_A_PAGE);
		compactionWriter = compactionWriterFor(pageChain);
		final long memberCountBeforeCompaction = pageChain.totalMemberCount();

		compact();

		assertThat(pageChain.pagesMarkedForDeletion())
				.as("only the three underutilised fragments are merged away. %s", pageChain.describe())
				.containsExactlyInAnyOrder(3L, 4L, 5L);
		assertThat(pageChain.pagesWithMembersThatAreUnreachableFromTheRoot())
				.as("the compacted fragment takes the place of fragments 3, 4 and 5 in the chain. %s",
						pageChain.describe())
				.isEmpty();
		assertThat(pageChain.pageThatIsVisitedTwiceWhenWalkingFromTheRoot())
				.as("the chain stays a path. %s", pageChain.describe())
				.isEmpty();
		assertThat(pageChain.reachableMemberCount())
				.as("no member is lost along the way. %s", pageChain.describe())
				.isEqualTo(memberCountBeforeCompaction);
	}

	/**
	 * Replays {@link CompactionTask#execute} without the Spring Batch plumbing around it.
	 */
	private void compact() {
		new CompactionCandidateSorter()
				.getSortedCompactionCandidates(pageChain.getCompactionCandidates(CAPACITY_PER_PAGE), CAPACITY_PER_PAGE)
				.forEach(compactionWriter::write);
	}

	private CompactionWriter compactionWriterFor(PageChain chain) {
		return new CompactionWriter(chain.relationRepository(), chain.memberRepository(),
				new CompactedFragmentCreator(jdbcTemplateFor(chain)), pageDeletionTimeSetterFor(chain),
				ObservationRegistry.NOOP);
	}

	/**
	 * A {@link JdbcTemplate} that applies the two statements of {@link CompactedFragmentCreator} to the in memory
	 * chain: the insert of the compacted page and the insert of its outgoing relation.
	 */
	private JdbcTemplate jdbcTemplateFor(PageChain chain) {
		final JdbcTemplate jdbcTemplate = mock();
		doAnswer(invocation -> {
			final long compactedPageId = chain.addCompactedPage();
			invocation.getArgument(1, KeyHolder.class).getKeyList().add(Map.of("page_id", compactedPageId));
			return 1;
		}).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
		doAnswer(invocation -> {
			chain.addRelation(((Number) invocation.getArgument(1)).longValue(),
					((Number) invocation.getArgument(2)).longValue());
			return 1;
		}).when(jdbcTemplate).update(eq(CompactedFragmentCreator.INSERT_PAGE_RELATION_SQL), any(), any(), anyString());
		return jdbcTemplate;
	}

	private PageDeletionTimeSetter pageDeletionTimeSetterFor(PageChain chain) {
		final PageDeletionTimeSetter pageDeletionTimeSetter = mock();
		doAnswer(invocation -> {
			chain.markForDeletion(invocation.getArgument(0));
			return null;
		}).when(pageDeletionTimeSetter).setDeleteTimeOfFragment(any());
		return pageDeletionTimeSetter;
	}

	/**
	 * An in memory stand in for the {@code pages}, {@code page_members} and {@code page_relations} tables, with the
	 * same semantics as the statements that compaction runs against them.
	 */
	private static final class PageChain {
		private final Map<Long, Integer> memberCountPerPage = new LinkedHashMap<>();
		private final Map<Long, Long> nextPageIdPerPage = new LinkedHashMap<>();
		private final List<long[]> relations = new ArrayList<>();
		private final Set<Long> pagesMarkedForDeletion = new HashSet<>();
		private long nextFreePageId = ROOT_PAGE_ID + 1;

		/**
		 * Builds {@code root -> 1 -> 2 -> ... -> n -> open page}, where every immutable page holds the same number of
		 * members and the open page is the one that is still being filled by fragmentation.
		 */
		static PageChain ofPagesWithEqualSize(int numberOfPages, int membersPerPage) {
			return ofPagesWithSizes(Collections.nCopies(numberOfPages, membersPerPage), membersPerPage);
		}

		/**
		 * Builds {@code root -> 1 -> 2 -> ... -> n -> open page}, where every immutable page holds the number of
		 * members at its position in {@code membersPerImmutablePage}.
		 */
		static PageChain ofPagesWithSizes(List<Integer> membersPerImmutablePage, int membersOnTheOpenPage) {
			final PageChain pageChain = new PageChain();
			pageChain.memberCountPerPage.put(ROOT_PAGE_ID, 0);
			for (long pageId = 1; pageId <= membersPerImmutablePage.size(); pageId++) {
				pageChain.memberCountPerPage.put(pageId, membersPerImmutablePage.get((int) pageId - 1));
				pageChain.nextPageIdPerPage.put(pageId, pageId + 1);
				pageChain.relations.add(new long[] {pageId == 1 ? ROOT_PAGE_ID : pageId - 1, pageId});
			}
			final long openPageId = membersPerImmutablePage.size() + 1L;
			pageChain.memberCountPerPage.put(openPageId, membersOnTheOpenPage);
			pageChain.relations.add(new long[] {membersPerImmutablePage.size(), openPageId});
			return pageChain;
		}

		/**
		 * The equivalent of {@code CompactionPageEntityRepository#findCompactionCandidates}: every immutable page of
		 * the view that is below capacity and that has an outgoing relation.
		 */
		List<CompactionCandidate> getCompactionCandidates(int capacityPerPage) {
			return nextPageIdPerPage.entrySet().stream()
					.filter(entry -> memberCountPerPage.get(entry.getKey()) < capacityPerPage)
					.map(entry -> new CompactionCandidate(entry.getKey(), memberCountPerPage.get(entry.getKey()),
							entry.getValue(), BUCKET_ID, "/ex/p?pageNumber=" + entry.getKey()))
					.toList();
		}

		long addCompactedPage() {
			final long compactedPageId = nextFreePageId++;
			memberCountPerPage.put(compactedPageId, 0);
			return compactedPageId;
		}

		void addRelation(long fromPageId, long toPageId) {
			relations.add(new long[] {fromPageId, toPageId});
		}

		void markForDeletion(List<Long> pageIds) {
			pagesMarkedForDeletion.addAll(pageIds);
		}

		Set<Long> pagesMarkedForDeletion() {
			return pagesMarkedForDeletion;
		}

		/**
		 * {@code UPDATE page_relations SET to_page_id = :targetId WHERE to_page_id IN :ids OR from_page_id IN :ids}
		 */
		CompactionPageRelationRepository relationRepository() {
			return (compactedPageIds, targetId) -> relations.stream()
					.filter(relation -> compactedPageIds.contains(relation[0]) || compactedPageIds.contains(relation[1]))
					.forEach(relation -> relation[1] = targetId);
		}

		/**
		 * {@code UPDATE page_members SET page_id = :newPageId WHERE page_id IN :pageIds}
		 */
		PageMemberRepository memberRepository() {
			final PageMemberRepository pageMemberRepository = mock();
			doAnswer(invocation -> {
				final long newPageId = invocation.getArgument(0);
				final List<Long> pageIds = invocation.getArgument(1);
				pageIds.forEach(pageId -> memberCountPerPage.merge(newPageId,
						memberCountPerPage.replace(pageId, 0), Integer::sum));
				return null;
			}).when(pageMemberRepository).setPageMembersToNewPage(anyLong(), any());
			return pageMemberRepository;
		}

		long totalMemberCount() {
			return memberCountPerPage.values().stream().mapToLong(Integer::longValue).sum();
		}

		List<Long> pagesWithMembersThatAreUnreachableFromTheRoot() {
			final Set<Long> reachablePages = reachablePages();
			return memberCountPerPage.entrySet().stream()
					.filter(page -> page.getValue() > 0)
					.filter(page -> !pagesMarkedForDeletion.contains(page.getKey()))
					.map(Map.Entry::getKey)
					.filter(pageId -> !reachablePages.contains(pageId))
					.toList();
		}

		long reachableMemberCount() {
			return reachablePages().stream()
					.mapToLong(pageId -> memberCountPerPage.getOrDefault(pageId, 0))
					.sum();
		}

		private Set<Long> reachablePages() {
			final Set<Long> visitedPages = new HashSet<>();
			final Deque<Long> pagesToVisit = new ArrayDeque<>(List.of(ROOT_PAGE_ID));
			while (!pagesToVisit.isEmpty()) {
				final long pageId = pagesToVisit.pop();
				if (visitedPages.add(pageId)) {
					successorsOf(pageId).forEach(pagesToVisit::push);
				}
			}
			return visitedPages;
		}

		/**
		 * A view is a chain of pages, so walking it from the root may never arrive at a page that was already visited.
		 *
		 * @return the first page that is visited twice, or empty when the chain is a proper path
		 */
		List<Long> pageThatIsVisitedTwiceWhenWalkingFromTheRoot() {
			final Set<Long> visitedPages = new HashSet<>();
			final Deque<Long> pagesToVisit = new ArrayDeque<>(List.of(ROOT_PAGE_ID));
			while (!pagesToVisit.isEmpty()) {
				final long pageId = pagesToVisit.pop();
				if (!visitedPages.add(pageId)) {
					return List.of(pageId);
				}
				successorsOf(pageId).forEach(pagesToVisit::push);
			}
			return List.of();
		}

		private List<Long> successorsOf(long pageId) {
			return relations.stream()
					.filter(relation -> relation[0] == pageId)
					.map(relation -> relation[1])
					.toList();
		}

		String describe() {
			final String chain = relations.stream()
					.map(relation -> "%d -> %d".formatted(relation[0], relation[1]))
					.toList()
					.toString();
			return "Relations: %s, members per page: %s, pages marked for deletion: %s"
					.formatted(chain, memberCountPerPage, pagesMarkedForDeletion);
		}
	}
}
