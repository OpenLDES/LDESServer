package org.openldes.server.admin.domain.eventsource.services;

import org.openldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

public interface EventSourceService {
    Optional<EventSource> getEventSource(String collectionName);

    void updateEventSource(String collectionName, List<Model> retentionPolicies);
}
