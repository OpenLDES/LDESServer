package org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators;

import org.openldes.server.admin.domain.validation.dcat.DcatNodeValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_CATALOG;

public class CannotContainCatalogValidator implements DcatNodeValidator {
	@Override
	public void validate(Model dcat) {
		if (dcat.listSubjectsWithProperty(RDF.type, DCAT_CATALOG).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a data catalog.");
		}
	}
}
