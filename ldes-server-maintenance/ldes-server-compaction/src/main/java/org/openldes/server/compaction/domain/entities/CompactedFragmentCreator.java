package org.openldes.server.compaction.domain.entities;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openldes.server.domain.constants.RdfConstants;
import org.openldes.server.domain.exceptions.PageListSortException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class CompactedFragmentCreator {
	public static final String PAGE_NUMBER_REGEX = "pageNumber=.*";
	public static final String INSERT_COMPACTED_PAGE_SQL = "INSERT INTO pages (bucket_id, expiration, partial_url, immutable) VALUES (?, NULL, ?, true)";
	public static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?) on conflict do nothing";
	private final JdbcTemplate jdbcTemplate;

	public CompactedFragmentCreator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

	public Long createCompactedPage(Collection<CompactionCandidate> pages) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final CompactionCandidate lastPage = getLastPageOfRun(pages);

		final String compactedPagePartialUrl = createCompactedPartialUrl(lastPage);
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(INSERT_COMPACTED_PAGE_SQL, new String[] {"page_id"});
			ps.setLong(1, lastPage.getBucketId());
			ps.setString(2, compactedPagePartialUrl);
			return ps;
		}, keyHolder);

		jdbcTemplate.update(INSERT_PAGE_RELATION_SQL, keyHolder.getKey(), lastPage.getNextPageId(), RdfConstants.GENERIC_TREE_RELATION);

		return keyHolder.getKeyAs(Long.class);
	}

	/**
	 * The page of the group that the compacted page takes the outgoing relation of, which is the only page whose
	 * successor lies outside of the group.
	 * <p>
	 * The pages that are merged have to form an uninterrupted run of the chain. Every page in front of the group is
	 * rewritten to point at the compacted page and every page inside of it disappears, so a group with a gap or with
	 * two heads leaves the pages in between either without an incoming relation or in a cycle with the compacted page.
	 * Such a group is refused instead of being written.
	 */
	private static CompactionCandidate getLastPageOfRun(Collection<CompactionCandidate> pages) {
		final List<CompactionCandidate> pagesWithoutSuccessorInTheGroup = pages.stream()
				.filter(page -> !containsPage(pages, page.getNextPageId()))
				.toList();

		if (pagesWithoutSuccessorInTheGroup.size() != 1
				|| !isUninterruptedRun(pages, pagesWithoutSuccessorInTheGroup.getFirst())) {
			throw new PageListSortException(pages.stream().map(p -> String.valueOf(p.getId())).toList());
		}

		return pagesWithoutSuccessorInTheGroup.getFirst();
	}

	/**
	 * Walks the group backwards from its last page: a run is uninterrupted when every page has at most one predecessor
	 * inside the group and the walk reaches every page of the group.
	 */
	private static boolean isUninterruptedRun(Collection<CompactionCandidate> pages, CompactionCandidate lastPage) {
		CompactionCandidate currentPage = lastPage;
		int visitedPages = 1;

		while (currentPage != null) {
			final CompactionCandidate pageToVisit = currentPage;
			final List<CompactionCandidate> predecessors = pages.stream()
					.filter(page -> page.getNextPageId() == pageToVisit.getId())
					.toList();
			if (predecessors.size() > 1) {
				return false;
			}
			currentPage = predecessors.isEmpty() ? null : predecessors.getFirst();
			visitedPages += predecessors.size();
		}

		return visitedPages == pages.size();
	}

	private static boolean containsPage(Collection<CompactionCandidate> pages, long pageId) {
		return pages.stream().anyMatch(page -> page.getId() == pageId);
	}

	private String createCompactedPartialUrl(CompactionCandidate candidate) {
		Matcher matcher = Pattern.compile(PAGE_NUMBER_REGEX).matcher(candidate.getPartialUrl());
		return matcher.replaceFirst("pageNumber=" + UUID.randomUUID());
	}
}
