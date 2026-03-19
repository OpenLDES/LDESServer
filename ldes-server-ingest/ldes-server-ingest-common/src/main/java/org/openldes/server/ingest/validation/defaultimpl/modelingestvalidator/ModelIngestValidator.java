package org.openldes.server.ingest.validation.defaultimpl.modelingestvalidator;

import org.apache.jena.rdf.model.Model;

public interface ModelIngestValidator {

	void validate(Model model);

}
