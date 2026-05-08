package org.openldes.server.admin.postgres.eventsource;

import java.util.List;
import java.util.Optional;
import org.openldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import org.openldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import org.openldes.server.admin.postgres.eventsource.mapper.EventSourceMapper;
import org.openldes.server.admin.postgres.eventsource.repository.EventSourceEntityRepository;
import org.openldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import org.openldes.server.domain.model.EventSource;
import org.springframework.stereotype.Repository;

@Repository
public class EventSourcePostgresRepository implements EventSourceRepository {
    private final EventSourceEntityRepository eventSourceEntityRepository;
    private final EventStreamEntityRepository eventStreamEntityRepository;

    public EventSourcePostgresRepository(EventSourceEntityRepository eventSourceEntityRepository, EventStreamEntityRepository eventStreamEntityRepository) {
        this.eventSourceEntityRepository = eventSourceEntityRepository;
        this.eventStreamEntityRepository = eventStreamEntityRepository;
    }

    @Override
    public void saveEventSource(EventSource eventSource) {
        eventSourceEntityRepository.findByCollectionName(eventSource.collectionName())
                .or(() -> eventStreamEntityRepository.findByName(eventSource.collectionName()).map(EventSourceEntity::new))
                .ifPresent(eventSourceEntity -> {
                    eventSourceEntity.setRetentionPolicies(eventSource.retentionPolicies());
                    eventSourceEntityRepository.save(eventSourceEntity);
                });
    }

    @Override
    public Optional<EventSource> getEventSource(String collectionName) {
        return eventSourceEntityRepository.findByCollectionName(collectionName).map(EventSourceMapper::fromEntity);
    }

    @Override
    public List<EventSource> getAllEventSources() {
        return eventSourceEntityRepository.findAll().stream()
                .map(EventSourceMapper::fromEntity)
                .toList();
    }
}
