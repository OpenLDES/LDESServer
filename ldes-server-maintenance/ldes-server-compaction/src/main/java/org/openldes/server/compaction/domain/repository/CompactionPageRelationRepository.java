package org.openldes.server.compaction.domain.repository;

import java.util.List;

public interface CompactionPageRelationRepository {
	void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId);
}
