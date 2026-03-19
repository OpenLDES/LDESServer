package org.openldes.server.maintenance.postgres.repository;

import org.openldes.server.maintenance.postgres.entity.CompactionPageRelationEntity;
import org.openldes.server.maintenance.postgres.entity.RelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompactionPageRelationEntityRepository extends JpaRepository<CompactionPageRelationEntity, RelationId> {

	@Modifying
	@Query("UPDATE CompactionPageRelationEntity r SET r.toPage = ( SELECT p FROM CompactionPageEntity p WHERE p.id = :targetId ) " +
	"WHERE r.toPage.id IN :ids OR r.fromPage.id IN : ids")
	void updateToPageRelations(List<Long> ids, long targetId);
}
