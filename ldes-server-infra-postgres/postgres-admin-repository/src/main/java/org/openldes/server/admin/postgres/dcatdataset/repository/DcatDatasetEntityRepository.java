package org.openldes.server.admin.postgres.dcatdataset.repository;

import java.util.Optional;
import org.openldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DcatDatasetEntityRepository extends JpaRepository<DcatDatasetEntity, Integer> {
    @Query("SELECT d FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    Optional<DcatDatasetEntity> findByCollectionName(String collectionName);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    boolean existsByCollectionName(String collectionName);

    @Query("DELETE FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    @Modifying
    void deleteByCollectionName(String collectionName);
}
