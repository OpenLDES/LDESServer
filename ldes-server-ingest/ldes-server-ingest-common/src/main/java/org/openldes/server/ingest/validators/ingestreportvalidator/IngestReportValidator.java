package org.openldes.server.ingest.validators.ingestreportvalidator;

import org.apache.jena.rdf.model.Model;
import org.openldes.server.domain.model.EventStream;

@FunctionalInterface
public interface IngestReportValidator {
    void validate(Model model, EventStream eventStream, ShaclReportManager reportManager);
}
