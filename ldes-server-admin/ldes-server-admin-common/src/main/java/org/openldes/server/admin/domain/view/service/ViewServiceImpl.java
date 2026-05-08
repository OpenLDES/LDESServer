package org.openldes.server.admin.domain.view.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.openldes.server.admin.domain.view.repository.ViewRepository;
import org.openldes.server.domain.events.admin.EventStreamCreatedEvent;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.events.admin.ViewAddedEvent;
import org.openldes.server.domain.events.admin.ViewDeletedEvent;
import org.openldes.server.domain.events.admin.ViewInitializationEvent;
import org.openldes.server.domain.exceptions.ExistingResourceException;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.domain.model.EventStream;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ViewServiceImpl implements ViewService {
    private static final Logger log = LoggerFactory.getLogger(ViewServiceImpl.class);
    private static final String EVENT_STREAM_TYPE = "eventstream";
    private static final String VIEW_TYPE = "view";
    private final DcatViewService dcatViewService;
    private final ViewRepository viewRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ViewValidator viewValidator;

    private final HashMap<String, EventStream> eventStreams = new HashMap<>();

    public ViewServiceImpl(DcatViewService dcatViewService, ViewRepository viewRepository,
                           ApplicationEventPublisher eventPublisher, ViewValidator viewValidator) {
        this.dcatViewService = dcatViewService;
        this.viewRepository = viewRepository;
        this.eventPublisher = eventPublisher;
	    this.viewValidator = viewValidator;
    }

    @Override
    public void addView(ViewSpecification viewSpecification) {
        log.atInfo().log("START creating view {}", viewSpecification.getName().asString());
        if (isEventStreamMissing(viewSpecification.getName().getCollectionName())) {
            throw new MissingResourceException(EVENT_STREAM_TYPE, viewSpecification.getName().getCollectionName());
        }

        checkIfViewAlreadyExists(viewSpecification);
        viewValidator.validateView(viewSpecification);

        viewRepository.saveView(viewSpecification);
        CompletableFuture.runAsync(() -> eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification)));
        log.atInfo().log("FINISHED creating view {}", viewSpecification.getName().asString());
    }

    private void checkIfViewAlreadyExists(ViewSpecification viewSpecification) {
        Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewSpecification.getName());
        if (view.isPresent()) {
            throw new ExistingResourceException(VIEW_TYPE, viewSpecification.getName().asString());
        }
    }

    private boolean isEventStreamMissing(String collectionName) {
        return !eventStreams.containsKey(collectionName);
    }

    @Override
    public ViewSpecification getViewByViewName(ViewName viewName) {
        ViewSpecification viewSpecification = viewRepository.getViewByViewName(viewName)
                .orElseThrow(() -> new MissingResourceException(VIEW_TYPE, viewName.asString()));
        addDcatToViewSpecification(viewSpecification);
        return viewSpecification;
    }

    @Override
    public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
        if (isEventStreamMissing(collectionName)) {
            throw new MissingResourceException(EVENT_STREAM_TYPE, collectionName);
        }
        List<ViewSpecification> viewSpecifications = viewRepository.retrieveAllViewsOfCollection(collectionName);
        viewSpecifications.forEach(this::addDcatToViewSpecification);
        return viewSpecifications;
    }

    private void addDcatToViewSpecification(ViewSpecification viewSpecification) {
        dcatViewService.findByViewName(viewSpecification.getName()).ifPresent(viewSpecification::setDcat);
    }

    @Override
    public void deleteViewByViewName(ViewName viewName) {
        if (isEventStreamMissing(viewName.getCollectionName())) {
            throw new MissingResourceException(EVENT_STREAM_TYPE, viewName.getCollectionName());
        }

        log.atInfo().log("START deleting view  {}", viewName.asString());
        viewRepository.deleteViewByViewName(viewName);
        log.atInfo().log("FINISHED deleting view {}", viewName.asString());
        eventPublisher.publishEvent(new ViewDeletedEvent(viewName));
    }

    /**
     * Initializes the views config.
     * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
     * to give db migrations time before this init.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initViews() {
        viewRepository
                .retrieveAllViews()
                .forEach(viewSpecification -> eventPublisher
                        .publishEvent(new ViewInitializationEvent(viewSpecification)));
    }

    @EventListener
    public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
        eventStreams.put(event.eventStream().getCollection(), event.eventStream());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        String collectionName = event.collectionName();
        eventStreams.remove(collectionName);
    }

}
