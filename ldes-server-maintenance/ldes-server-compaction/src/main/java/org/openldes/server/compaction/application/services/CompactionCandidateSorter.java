package org.openldes.server.compaction.application.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.openldes.server.compaction.application.valueobjects.CompactedPages;
import org.openldes.server.compaction.application.valueobjects.CompactionCandidates;
import org.openldes.server.compaction.application.valueobjects.CompactionPageCapacity;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;
import org.springframework.stereotype.Component;

@Component
public class CompactionCandidateSorter {

	public List<Set<CompactionCandidate>> getSortedCompactionCandidates(List<CompactionCandidate> candidates,
	                                                                    int capacityPerPage) {
		final CompactionCandidates compactionCandidates = new CompactionCandidates(candidates);
		return compactionCandidates
				.getLeadingPages().stream()
				.flatMap(leadingPage -> getCompactedCandidatesForLeadingPage(leadingPage, compactionCandidates, capacityPerPage).stream())
				.toList();
	}

	/**
	 * Walks the chain that starts at the given leading page and cuts it into runs of pages that follow each other and
	 * that together fit into a single page. Every chain starts from an empty page: the capacity that was used while
	 * walking one chain may not count towards the capacity of the next one.
	 */
	private List<Set<CompactionCandidate>> getCompactedCandidatesForLeadingPage(CompactionCandidate leadingPage,
	                                                                            CompactionCandidates candidates,
	                                                                            int capacityPerPage) {
		CompactedPages compactedPages = new CompactedPages();
		CompactionPageCapacity capacity = new CompactionPageCapacity(capacityPerPage);
		Optional<CompactionCandidate> currentCandidate = Optional.of(leadingPage);

		while (currentCandidate.isPresent()) {
			addCandidateToCompactedPage(capacity, currentCandidate.get(), compactedPages);
			currentCandidate = candidates.getNextCandidate(currentCandidate.get().getNextPageId());
		}

		compactedPages.closeCompactedPage();

		return compactedPages.getPages();
	}

	/**
	 * A candidate that no longer fits into the page that is being filled starts the next page instead of being
	 * skipped: skipping it would hand the page after it to the same compacted page, which merges pages that are not
	 * adjacent in the chain and leaves the skipped page without an incoming relation.
	 */
	private void addCandidateToCompactedPage(CompactionPageCapacity compactionPageCapacity,
	                                         CompactionCandidate candidate,
	                                         CompactedPages compactedPages) {
		if (!compactionPageCapacity.hasRoomFor(candidate.getSize())) {
			compactedPages.closeCompactedPage();
			compactionPageCapacity.reset();
		}
		compactionPageCapacity.increase(candidate.getSize());
		compactedPages.addCompactionCandidate(candidate);
	}

}
