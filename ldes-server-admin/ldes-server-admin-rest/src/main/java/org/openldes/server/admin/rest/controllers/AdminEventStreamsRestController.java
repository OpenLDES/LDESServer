package org.openldes.server.admin.rest.controllers;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.domain.eventstream.services.EventStreamService;
import org.openldes.server.admin.domain.validation.ModelValidator;
import org.openldes.server.admin.spi.EventStreamReader;
import org.openldes.server.admin.spi.EventStreamTO;
import org.openldes.server.admin.spi.RetentionModelExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Observed
@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams")
public class AdminEventStreamsRestController implements OpenApiAdminEventStreamsController {

    private static final Logger log = LoggerFactory.getLogger(AdminEventStreamsRestController.class);

    private final EventStreamService eventStreamService;
    private final EventStreamReader eventStreamReader;
    private final RetentionModelExtractor retentionModelExtractor;
    private final ModelValidator eventStreamValidator;
    private final ModelValidator eventSourceValidator;

    public AdminEventStreamsRestController(EventStreamService eventStreamService,
                                           @Qualifier("eventStreamShaclValidator") ModelValidator eventStreamValidator,
                                           @Qualifier("eventSourceShaclValidator") ModelValidator eventSourceValidator,
                                           EventStreamReader eventStreamReader, RetentionModelExtractor retentionModelExtractor) {
        this.eventStreamService = eventStreamService;
        this.eventStreamValidator = eventStreamValidator;
        this.eventSourceValidator = eventSourceValidator;
        this.eventStreamReader = eventStreamReader;
        this.retentionModelExtractor = retentionModelExtractor;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(eventStreamValidator);
    }

    @Override
    @GetMapping
    public List<EventStreamTO> getEventStreams() {
        return eventStreamService.retrieveAllEventStreams();
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @Override
    @PostMapping(consumes = {contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle})
    public EventStreamTO createEventStream(@RequestBody Model eventStreamModel) {
        eventStreamValidator.validate(eventStreamModel);
        EventStreamTO eventStreamTO = eventStreamReader.read(eventStreamModel);
        log.atInfo().log("START creating collection {}", eventStreamTO.getCollection());
        eventStreamService.createEventStream(eventStreamTO);
        log.atInfo().log("FINISHED creating collection {}", eventStreamTO.getCollection());
        return eventStreamTO;
    }

    @Override
    @GetMapping("/{collectionName}")
    public EventStreamTO getEventStream(@PathVariable String collectionName) {
        return eventStreamService.retrieveEventStream(collectionName);
    }

    @Override
    @DeleteMapping("/{collectionName}")
    public void deleteEventStream(@PathVariable String collectionName) {
        log.atInfo().log("START deleting collection {}", collectionName);
        eventStreamService.deleteEventStream(collectionName);
        log.atInfo().log("FINISHED deleting collection {}", collectionName);
    }

    @Override
    @PostMapping("/{collectionName}/close")
    public void closeEventStream(@PathVariable String collectionName) {
        log.atInfo().log("START closing collection {}", collectionName);
        eventStreamService.closeEventStream(collectionName);
        log.atInfo().log("FINISHED closing collection {}", collectionName);
    }

    @Override
    @PutMapping("/{collectionName}/eventsource")
    public void updateEventSource(@PathVariable String collectionName, @RequestBody Model eventSourceModel) {
        eventSourceValidator.validate(eventSourceModel);
        List<Model> retentionPolicies = retentionModelExtractor.extractRetentionStatements(eventSourceModel);
        eventStreamService.updateEventSource(collectionName, retentionPolicies);
    }
}
