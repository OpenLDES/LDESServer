package org.openldes.server.maintenance.postgres;

import java.time.LocalDateTime;
import java.util.List;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;
import org.openldes.server.compaction.domain.repository.CompactionPageRepository;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.maintenance.postgres.mapper.CompactionCandidateMapper;
import org.openldes.server.maintenance.postgres.repository.CompactionPageEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CompactionPagePostgresRepository implements CompactionPageRepository {
	private final CompactionPageEntityRepository pageEntityRepository;

	public CompactionPagePostgresRepository(CompactionPageEntityRepository pageEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
	}

	@Override
	public List<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {
		return pageEntityRepository
				.findCompactionCandidates(viewName.getCollectionName(), viewName.getViewName(), capacityPerPage)
				.stream()
				.map(CompactionCandidateMapper::fromProjection)
				.toList();
	}

	@Override
	@Transactional
	public void deleteOutdatedFragments(LocalDateTime deleteTime) {
		pageEntityRepository.deleteByExpirationBefore(deleteTime);
	}

	@Override
	@Transactional
	public void setDeleteTime(List<Long> ids, LocalDateTime deleteTime) {
		pageEntityRepository.setDeleteTime(ids, deleteTime);
	}
}
