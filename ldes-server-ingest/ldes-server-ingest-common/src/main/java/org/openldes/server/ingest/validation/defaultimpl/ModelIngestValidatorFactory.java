package org.openldes.server.ingest.validation.defaultimpl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.openldes.server.ingest.validation.defaultimpl.modelingestvalidator.EmptyValidator;
import org.openldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import org.openldes.server.ingest.validation.defaultimpl.modelingestvalidator.ShaclModelValidator;

public class ModelIngestValidatorFactory {

	public ModelIngestValidator createValidator(final Model shaclShape) {
		if (shaclShape != null && !shaclShape.isEmpty()) {
			return new ShaclModelValidator(Shapes.parse(shaclShape));
		}

		return new EmptyValidator();
	}

}
