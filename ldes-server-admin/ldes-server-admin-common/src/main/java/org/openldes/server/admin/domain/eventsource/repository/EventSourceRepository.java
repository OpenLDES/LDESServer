package org.openldes.server.admin.domain.eventsource.repository;

import org.openldes.server.domain.model.EventSource;

import java.util.List;
import java.util.Optional;

public interface EventSourceRepository {
    void saveEventSource(EventSource eventSource);

    Optional<EventSource> getEventSource(String collectionName);

    List<EventSource> getAllEventSources();
}
