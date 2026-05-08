package org.openldes.server.admin.postgres.shaclshape.repository;

import java.util.Optional;
import org.openldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShaclShapeEntityRepository extends JpaRepository<ShaclShapeEntity, Integer> {
    @Query("SELECT s FROM ShaclShapeEntity s WHERE s.eventStream.name = :collectionName")
    Optional<ShaclShapeEntity> findByCollectionName(String collectionName);

    @Modifying
    @Query("DELETE FROM ShaclShapeEntity s WHERE s.eventStream.name = :collectionName")
    void deleteByCollectionName(String collectionName);

}
