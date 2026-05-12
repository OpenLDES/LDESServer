package org.openldes.server.pagination.postgres;

import java.util.List;
import org.openldes.server.domain.model.FragmentationMetric;
import org.openldes.server.domain.services.FragmentationMetricsRepository;
import org.openldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FragmentationMetricsPostgresRepository implements FragmentationMetricsRepository {
	private final PageMemberEntityRepository entityRepository;

	public FragmentationMetricsPostgresRepository(PageMemberEntityRepository entityRepository) {
		this.entityRepository = entityRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FragmentationMetric> getBucketisedMemberCounts(String collectionName) {
		return entityRepository.getBucketisedMemberCounts(collectionName)
				.stream()
				.map(tuple -> new FragmentationMetric(tuple.get(0, String.class), tuple.get(1, Long.class).intValue()))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<FragmentationMetric> getPaginatedMemberCounts(String collectionName) {
		return entityRepository.getPaginatedMemberCounts(collectionName)
				.stream()
				.map(tuple -> new FragmentationMetric(tuple.get(0, String.class), tuple.get(1, Long.class).intValue()))
				.toList();
	}
}
