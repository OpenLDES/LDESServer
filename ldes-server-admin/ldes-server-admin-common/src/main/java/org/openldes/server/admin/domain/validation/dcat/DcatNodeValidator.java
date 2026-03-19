package org.openldes.server.admin.domain.validation.dcat;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface DcatNodeValidator {
	void validate(Model dcat);
}
