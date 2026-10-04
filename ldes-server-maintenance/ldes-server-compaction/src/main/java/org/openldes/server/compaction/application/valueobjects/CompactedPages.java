package org.openldes.server.compaction.application.valueobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;

public class CompactedPages {
	private final List<Set<CompactionCandidate>> pages;
	private final Set<CompactionCandidate> compactedCandidatesToAdd;


	public CompactedPages() {
		pages = new ArrayList<>();
		compactedCandidatesToAdd = new HashSet<>();
	}

	/**
	 * Ends the page that is being filled: it only becomes a compacted page when it holds more than one candidate,
	 * since merging a single page into a page of its own has no effect. Either way the candidates are let go of: a
	 * candidate that stays behind is no longer the neighbour of the candidate that is added next, so keeping it would
	 * merge pages that do not follow each other in the chain.
	 */
	public void closeCompactedPage() {
		if (compactedCandidatesToAdd.size() > 1) {
			pages.add(Set.copyOf(compactedCandidatesToAdd));
		}
		compactedCandidatesToAdd.clear();
	}

	public void addCompactionCandidate(CompactionCandidate candidate) {
		compactedCandidatesToAdd.add(candidate);
	}

	public List<Set<CompactionCandidate>> getPages() {
		return List.copyOf(pages);
	}
}
