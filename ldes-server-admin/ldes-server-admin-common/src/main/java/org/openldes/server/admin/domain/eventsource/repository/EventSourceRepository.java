package org.openldes.server.admin.domain.eventsource.repository;

import java.util.List;
import java.util.Optional;
import org.openldes.server.domain.model.EventSource;

public interface EventSourceRepository {
    void saveEventSource(EventSource eventSource);

    Optional<EventSource> getEventSource(String collectionName);

    List<EventSource> getAllEventSources();
}
