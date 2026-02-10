package org.openldes.server.pagination.postgres.repository;

import org.openldes.server.pagination.postgres.entity.PageRelationEntity;
import org.openldes.server.pagination.postgres.entity.RelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PageRelationEntityRepository extends JpaRepository<PageRelationEntity, RelationId> {

	@Modifying
	@Query(value = """
			INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?)
			on conflict do nothing
			""", nativeQuery = true)
	void insertRelation(Long fromPageId, Long toPageId, String treeRelationType);
}
