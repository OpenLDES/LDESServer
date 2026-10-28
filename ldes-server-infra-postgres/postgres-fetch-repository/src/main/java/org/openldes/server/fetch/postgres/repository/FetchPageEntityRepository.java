package org.openldes.server.fetch.postgres.repository;


import java.util.List;
import java.util.Optional;
import org.openldes.server.fetch.postgres.entity.FetchPageEntity;
import org.openldes.server.fetch.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FetchPageEntityRepository extends JpaRepository<FetchPageEntity, Long> {
	@Transactional(readOnly = true)
	Optional<TreeNodeProjection> findTreeNodeProjectionByPartialUrl(String partialUrl);

	/**
	 * Find the persisted URL for a reordered request. The expression index added by
	 * Liquibase makes this lookup efficient even when the requested page is missing.
	 */
	@Transactional(readOnly = true)
	@Query(value = """
			SELECT partial_url FROM pages
			WHERE ldes_canonical_fragment_url(partial_url) = ldes_canonical_fragment_url(:partialUrl)
			LIMIT 2
			""", nativeQuery = true)
	List<String> findPartialUrlsWithEquivalentParameters(@Param("partialUrl") String partialUrl);
}
