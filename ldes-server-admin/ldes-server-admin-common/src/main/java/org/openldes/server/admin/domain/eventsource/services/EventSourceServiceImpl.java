package org.openldes.server.admin.domain.eventsource.services;

import java.util.List;
import java.util.Optional;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import org.openldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.domain.model.EventSource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EventSourceServiceImpl implements EventSourceService {
    private final EventSourceRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public EventSourceServiceImpl(EventSourceRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<EventSource> getEventSource(String collectionName) {
        return repository.getEventSource(collectionName);
    }

    @Override
    public void updateEventSource(String collectionName, List<Model> retentionPolicies) {
        if(repository.getEventSource(collectionName).isEmpty()) {
            throw new MissingResourceException("eventstream", collectionName);
        }
        repository.saveEventSource(new EventSource(collectionName, retentionPolicies));
        eventPublisher.publishEvent(new DeletionPolicyChangedEvent(collectionName, retentionPolicies));
    }


    /**
     * Initializes the eventSources.
     * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
     * to give db migrations time before this init.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initViews() {
        repository
                .getAllEventSources()
                .forEach(eventSource -> eventPublisher
                        .publishEvent(new DeletionPolicyChangedEvent(eventSource.collectionName(), eventSource.retentionPolicies())));
    }
}
