package org.openldes.server.admin.postgres.eventsource.repository;

import java.util.Optional;
import org.openldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventSourceEntityRepository extends JpaRepository<EventSourceEntity, Integer> {
    @Query("SELECT e FROM EventSourceEntity e WHERE e.eventStream.name = :collectionName")
    Optional<EventSourceEntity> findByCollectionName(String collectionName);
}
