package org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators;

import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_CATALOG;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.openldes.server.admin.domain.validation.dcat.DcatNodeValidator;

public class CannotContainCatalogValidator implements DcatNodeValidator {
	@Override
	public void validate(Model dcat) {
		if (dcat.listSubjectsWithProperty(RDF.type, DCAT_CATALOG).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a data catalog.");
		}
	}
}
