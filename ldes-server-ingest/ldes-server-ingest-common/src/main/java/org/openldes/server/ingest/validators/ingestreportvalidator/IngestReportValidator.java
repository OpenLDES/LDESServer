package org.openldes.server.ingest.validators.ingestreportvalidator;

import org.openldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface IngestReportValidator {
    void validate(Model model, EventStream eventStream, ShaclReportManager reportManager);
}
