package org.openldes.server.ingest.postgres;

import org.openldes.server.domain.services.MemberMetricsRepository;
import org.openldes.server.ingest.postgres.repository.MemberEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IngestMetricsPostgresRepository implements MemberMetricsRepository {
	private final MemberEntityRepository repository;

	public IngestMetricsPostgresRepository(MemberEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public int getTotalCount(String collectionName) {
		return repository.countMemberEntitiesByColl(collectionName);
	}
}
