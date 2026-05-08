package org.openldes.server.admin.domain.eventsource.services;

import java.util.List;
import java.util.Optional;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.domain.model.EventSource;

public interface EventSourceService {
    Optional<EventSource> getEventSource(String collectionName);

    void updateEventSource(String collectionName, List<Model> retentionPolicies);
}
