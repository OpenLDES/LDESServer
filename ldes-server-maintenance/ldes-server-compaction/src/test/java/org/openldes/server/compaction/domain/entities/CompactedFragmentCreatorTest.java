package org.openldes.server.compaction.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.constants.RdfConstants;
import org.openldes.server.domain.exceptions.PageListSortException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

class CompactedFragmentCreatorTest {
	private static final long COMPACTED_PAGE_ID = 10L;
	private static final CompactionCandidate PAGE_1 = new CompactionCandidate(1L, 3, 2L, 1L, "/ex/p?pageNumber=1");
	private static final CompactionCandidate PAGE_2 = new CompactionCandidate(2L, 3, 3L, 1L, "/ex/p?pageNumber=2");
	private static final CompactionCandidate PAGE_3 = new CompactionCandidate(3L, 3, 4L, 1L, "/ex/p?pageNumber=3");

	private JdbcTemplate jdbcTemplate;
	private CompactedFragmentCreator compactedFragmentCreator;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock();
		compactedFragmentCreator = new CompactedFragmentCreator(jdbcTemplate);
		doAnswer(invocation -> {
			invocation.getArgument(1, KeyHolder.class).getKeyList().add(Map.of("page_id", COMPACTED_PAGE_ID));
			return 1;
		}).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
	}

	@Test
	void given_AContiguousRun_when_CreatingACompactedPage_then_ItPointsToTheSuccessorOfTheLastPage() {
		final Long compactedPageId = compactedFragmentCreator.createCompactedPage(orderedSetOf(PAGE_1, PAGE_2, PAGE_3));

		assertThat(compactedPageId).isEqualTo(COMPACTED_PAGE_ID);
		verify(jdbcTemplate).update(CompactedFragmentCreator.INSERT_PAGE_RELATION_SQL, COMPACTED_PAGE_ID,
				PAGE_3.getNextPageId(), RdfConstants.GENERIC_TREE_RELATION);
	}

	/**
	 * Reproduction of part of <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a>: when the pages
	 * that are merged are not adjacent, both of them qualify as "the page whose successor is not in the set" and the
	 * creator picks an arbitrary one. The relation it then inserts points back into the range that is being compacted
	 * ({@code compacted -> 2} while page 2 is rewritten to {@code 2 -> compacted}), which closes the chain into a
	 * cycle and leaves everything behind it unreachable. Compaction has to refuse such a set instead.
	 */
	@Test
	void given_NonAdjacentPages_when_CreatingACompactedPage_then_ItIsRefused() {
		final Set<CompactionCandidate> nonAdjacentPages = orderedSetOf(PAGE_1, PAGE_3);

		assertThatThrownBy(() -> compactedFragmentCreator.createCompactedPage(nonAdjacentPages))
				.as("pages 1 and 3 do not form a run of the chain, so there is no single last page to link from")
				.isInstanceOf(PageListSortException.class);

		verify(jdbcTemplate, never()).update(anyString(), any(), any(), any());
	}

	/**
	 * The mirror image of the above: a set without a single first page is just as broken, since the pages in front of
	 * both of them are rewritten to point at the compacted page.
	 */
	@Test
	void given_PagesFromTwoDifferentRuns_when_CreatingACompactedPage_then_ItIsRefused() {
		final CompactionCandidate pageOfAnotherRun = new CompactionCandidate(7L, 3, 2L, 1L, "/ex/p?pageNumber=7");

		assertThatThrownBy(() -> compactedFragmentCreator.createCompactedPage(orderedSetOf(PAGE_1, pageOfAnotherRun)))
				.as("pages 1 and 7 both point at page 2, so they are not a run of the chain")
				.isInstanceOf(PageListSortException.class);
	}

	private static Set<CompactionCandidate> orderedSetOf(CompactionCandidate... candidates) {
		return new LinkedHashSet<>(List.of(candidates));
	}
}
