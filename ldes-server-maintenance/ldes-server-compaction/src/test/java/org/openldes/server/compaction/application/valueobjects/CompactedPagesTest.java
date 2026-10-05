package org.openldes.server.compaction.application.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;

class CompactedPagesTest {
	private static final CompactionCandidate PAGE_1 = new CompactionCandidate(1L, 95, 2L, 1L, "/ex/p?pageNumber=1");
	private static final CompactionCandidate PAGE_2 = new CompactionCandidate(2L, 89, 3L, 1L, "/ex/p?pageNumber=2");
	private static final CompactionCandidate PAGE_3 = new CompactionCandidate(3L, 95, 4L, 1L, "/ex/p?pageNumber=3");

	@Test
	void given_MoreThanOneOpenCandidate_when_ClosingThePage_then_TheCandidatesBecomeACompactedPage() {
		final CompactedPages compactedPages = new CompactedPages();

		compactedPages.addCompactionCandidate(PAGE_1);
		compactedPages.addCompactionCandidate(PAGE_2);
		compactedPages.closeCompactedPage();

		assertThat(compactedPages.getPages()).containsExactly(Set.of(PAGE_1, PAGE_2));
	}

	/**
	 * Regression test for part of <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a>: closing a
	 * page that only holds a single candidate used to leave that candidate open, so it was joined by the next
	 * candidate that fits. By then that candidate is no longer its neighbour in the chain, which is how the sorter
	 * ended up grouping pages 1 and 3 while leaving page 2 behind.
	 */
	@Test
	void given_ASingleOpenCandidate_when_ClosingThePage_then_TheCandidateIsNotCarriedOverToTheNextPage() {
		final CompactedPages compactedPages = new CompactedPages();

		compactedPages.addCompactionCandidate(PAGE_1);
		compactedPages.closeCompactedPage();
		compactedPages.addCompactionCandidate(PAGE_3);
		compactedPages.closeCompactedPage();

		assertThat(compactedPages.getPages())
				.as("a page of one is not compacted, and closing it may not leave page 1 open for page 3 to join")
				.isEmpty();
	}
}
