package org.openldes.server.compaction.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;
import org.openldes.server.domain.model.ViewName;

public interface CompactionPageRepository {
	List<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);
	void deleteOutdatedFragments(LocalDateTime deleteTime);
	void setDeleteTime(List<Long> ids, LocalDateTime deleteTime);
}
