package org.openldes.server.fetch.postgres.repository;


import org.openldes.server.fetch.postgres.entity.FetchPageEntity;
import org.openldes.server.fetch.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FetchPageEntityRepository extends JpaRepository<FetchPageEntity, Long> {
	@Transactional(readOnly = true)
	Optional<TreeNodeProjection> findTreeNodeProjectionByPartialUrl(String partialUrl);
}