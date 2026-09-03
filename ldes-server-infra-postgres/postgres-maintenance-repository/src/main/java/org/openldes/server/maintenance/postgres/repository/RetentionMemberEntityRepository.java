package org.openldes.server.maintenance.postgres.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.openldes.server.maintenance.postgres.entity.RetentionMemberEntity;
import org.openldes.server.maintenance.postgres.projection.RetentionMemberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RetentionMemberEntityRepository extends JpaRepository<RetentionMemberEntity, String> {

    void deleteAllByIdIn(List<Long> ids);
    @Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN EXISTS (SELECT 1 FROM RetentionPageMemberEntity p WHERE p.memberId = m.id) THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, v.eventStream.name AS collectionName FROM RetentionMemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND v.eventStream.name = :collectionName AND CAST(m.timestamp as timestamp) < CAST(:timestamp as timestamp) GROUP BY m.id, v.eventStream.name")
    List<RetentionMemberProjection> findAllByViewNameAndTimestampBefore(String viewName, String collectionName, LocalDateTime timestamp);

    @Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN EXISTS (SELECT 1 FROM RetentionPageMemberEntity p WHERE p.memberId = m.id) THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, v.eventStream.name AS collectionName FROM RetentionMemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND v.eventStream.name = :collectionName GROUP BY m.id, v.eventStream.name")
    List<RetentionMemberProjection> findAllByViewName(String viewName, String collectionName);

    @Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN EXISTS (SELECT 1 FROM RetentionPageMemberEntity p WHERE p.memberId = m.id) THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, c.name AS collectionName FROM RetentionMemberEntity m JOIN EventStreamEntity c ON m.collection = c WHERE c.name = :collectionName GROUP BY m.id, c.name")
    List<RetentionMemberProjection> findAllByCollectionName(String collectionName);

    @Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN EXISTS (SELECT 1 FROM RetentionPageMemberEntity p WHERE p.memberId = m.id) THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, c.name AS collectionName FROM RetentionMemberEntity m JOIN EventStreamEntity c ON m.collection = c WHERE c.name = :collectionName AND CAST(m.timestamp AS timestamp) < CAST(:timestamp as timestamp) GROUP BY m.id, c.name")
    List<RetentionMemberProjection> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
