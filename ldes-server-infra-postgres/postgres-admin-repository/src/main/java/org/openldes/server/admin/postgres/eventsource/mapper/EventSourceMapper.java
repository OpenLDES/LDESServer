package org.openldes.server.admin.postgres.eventsource.mapper;

import org.openldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import org.openldes.server.domain.model.EventSource;

public class EventSourceMapper {
    private EventSourceMapper() {
    }

    public static EventSource fromEntity(EventSourceEntity eventSourceEntity) {
        return new EventSource(eventSourceEntity.getCollectionName(), eventSourceEntity.getRetentionPolicies());
    }
}
