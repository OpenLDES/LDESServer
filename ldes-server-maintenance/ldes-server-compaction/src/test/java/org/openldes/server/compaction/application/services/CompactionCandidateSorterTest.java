package org.openldes.server.compaction.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;

class CompactionCandidateSorterTest {

	private static final List<CompactionCandidate> compactableCandidates = List.of(
			new CompactionCandidate(1L, 2, 2L, 1L, "/ex/p"),
			new CompactionCandidate(2L, 3, 3L, 1L, "/ex/p"),
			new CompactionCandidate(3L, 8, 4L, 1L, "/ex/p"),
			new CompactionCandidate(4L, 3, 5L, 1L, "/ex/p"),
			new CompactionCandidate(5L, 3, 6L, 1L, "/ex/p")
	);


	@Test
	void testGetCompactionTaskList() {
		final Collection<Set<CompactionCandidate>> taskList = new CompactionCandidateSorter()
				.getSortedCompactionCandidates(compactableCandidates, 10);

		assertThat(taskList)
				.hasSize(2)
				.allSatisfy(set -> assertThat(set).hasSize(2))
				.map(set -> set.stream().map(CompactionCandidate::getId).sorted().toList())
				.containsExactlyInAnyOrder(List.of(1L, 2L), List.of(4L, 5L));
	}

	/**
	 * Regression tests for <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a>: after a version
	 * based retention policy has run, the pages of a view are below capacity but still more than half full. The sorter
	 * used to group pages that are not adjacent in the chain, which the writer later turned into a chain with orphans
	 * and a cycle.
	 */
	@Nested
	class Issue52 {

		/**
		 * The shape a view has after retention removed a couple of superseded versions from every page: four adjacent
		 * pages of a view with {@code tree:pageSize 100}, each holding 89 to 95 members. No two neighbours fit into a
		 * single page of 100 members, so there is nothing to compact.
		 */
		private final List<CompactionCandidate> postRetentionCandidates = List.of(
				new CompactionCandidate(1L, 95, 2L, 1L, "/ex/p?pageNumber=1"),
				new CompactionCandidate(2L, 89, 3L, 1L, "/ex/p?pageNumber=2"),
				new CompactionCandidate(3L, 95, 4L, 1L, "/ex/p?pageNumber=3"),
				new CompactionCandidate(4L, 91, 5L, 1L, "/ex/p?pageNumber=4")
		);

		@Test
		void given_NoTwoNeighboursFitTogether_when_SortingCandidates_then_NothingIsCompacted() {
			final List<Set<CompactionCandidate>> compactedPages = new CompactionCandidateSorter()
					.getSortedCompactionCandidates(postRetentionCandidates, 100);

			assertThat(compactedPages)
					.as("95 + 89, 89 + 95 and 95 + 91 all exceed the capacity of 100, so no pages can be merged")
					.isEmpty();
		}

		/**
		 * The page that does not fit into the page being filled must start the next run instead of being skipped:
		 * dropping it is what allows the next page but one to be merged with the current run.
		 */
		@Test
		void given_NoTwoNeighboursFitTogether_when_SortingCandidates_then_NoCandidateIsSilentlyDropped() {
			final List<Set<CompactionCandidate>> compactedPages = new CompactionCandidateSorter()
					.getSortedCompactionCandidates(postRetentionCandidates, 100);

			assertThat(compactedPages.stream().flatMap(Set::stream).map(CompactionCandidate::getId).toList())
					.as("every compacted page only contains pages that were handed to the sorter, each at most once")
					.doesNotHaveDuplicates();
			assertEveryCompactedPageIsAContiguousRun(compactedPages, postRetentionCandidates);
		}

		/**
		 * A page that is left open because it only holds a single candidate must not be joined by a later candidate:
		 * by then that candidate is no longer the neighbour of the page that is still open.
		 */
		@Test
		void given_ALonePageFollowedByAFittingPage_when_SortingCandidates_then_OnlyNeighboursAreGrouped() {
			final List<CompactionCandidate> candidates = List.of(
					new CompactionCandidate(1L, 6, 2L, 1L, "/ex/p?pageNumber=1"),
					new CompactionCandidate(2L, 6, 3L, 1L, "/ex/p?pageNumber=2"),
					new CompactionCandidate(3L, 2, 4L, 1L, "/ex/p?pageNumber=3"),
					new CompactionCandidate(4L, 2, 5L, 1L, "/ex/p?pageNumber=4")
			);

			final List<Set<CompactionCandidate>> compactedPages = new CompactionCandidateSorter()
					.getSortedCompactionCandidates(candidates, 10);

			assertEveryCompactedPageIsAContiguousRun(compactedPages, candidates);
			assertThat(compactedPages)
					.as("page 1 does not fit together with page 2, so the only run that can be merged is 2 + 3 + 4")
					.hasSize(1)
					.map(page -> page.stream().map(CompactionCandidate::getId).sorted().toList())
					.containsExactly(List.of(2L, 3L, 4L));
		}

		/**
		 * The capacity that is used while walking a chain is per chain: the pages of one chain may never count towards
		 * the capacity of the next chain.
		 */
		@Test
		void given_TwoSeparateChains_when_SortingCandidates_then_TheUsedCapacityDoesNotLeakIntoTheNextChain() {
			final List<CompactionCandidate> candidates = List.of(
					new CompactionCandidate(1L, 4, 2L, 1L, "/ex/p?pageNumber=1"),
					new CompactionCandidate(2L, 4, 3L, 1L, "/ex/p?pageNumber=2"),
					new CompactionCandidate(10L, 4, 11L, 2L, "/ex/q?pageNumber=10"),
					new CompactionCandidate(11L, 4, 12L, 2L, "/ex/q?pageNumber=11")
			);

			final List<Set<CompactionCandidate>> compactedPages = new CompactionCandidateSorter()
					.getSortedCompactionCandidates(candidates, 10);

			assertThat(compactedPages)
					.as("both chains hold two pages of 4 members, so both of them can be compacted")
					.hasSize(2)
					.map(page -> page.stream().map(CompactionCandidate::getId).sorted().toList())
					.containsExactlyInAnyOrder(List.of(1L, 2L), List.of(10L, 11L));
		}

		/**
		 * Asserts the invariant that compaction may only merge pages that follow each other in the chain: a compacted
		 * page that absorbs pages 1 and 3 leaves page 2 without an incoming relation once the relations are rewritten.
		 */
		private void assertEveryCompactedPageIsAContiguousRun(List<Set<CompactionCandidate>> compactedPages,
		                                                      List<CompactionCandidate> chain) {
			final List<Long> chainOrder = chain.stream().map(CompactionCandidate::getId).toList();

			assertThat(compactedPages).allSatisfy(compactedPage -> {
				final List<Integer> positions = compactedPage.stream()
						.map(CompactionCandidate::getId)
						.map(chainOrder::indexOf)
						.sorted()
						.toList();
				final List<Integer> expectedPositions = IntStream
						.range(positions.getFirst(), positions.getFirst() + positions.size())
						.boxed()
						.toList();

				assertThat(positions)
						.as("the pages %s that are merged into one page have to follow each other in the chain %s",
								compactedPage, chainOrder)
						.containsExactlyElementsOf(expectedPositions);
			});
		}
	}
}
