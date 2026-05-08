package org.openldes.server.maintenance.postgres;

import java.util.List;
import org.openldes.server.compaction.domain.repository.CompactionPageRelationRepository;
import org.openldes.server.maintenance.postgres.repository.CompactionPageRelationEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CompactionPageRelationPostgresRepository implements CompactionPageRelationRepository {
	private final CompactionPageRelationEntityRepository pageRelationEntityRepository;

	public CompactionPageRelationPostgresRepository(CompactionPageRelationEntityRepository pageRelationEntityRepository) {
		this.pageRelationEntityRepository = pageRelationEntityRepository;
	}

	@Override
	@Transactional
	public void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId) {
		pageRelationEntityRepository.updateToPageRelations(compactedPageIds, targetId);
	}
}
