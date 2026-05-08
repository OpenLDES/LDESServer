package org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators;

import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_DATASET;
import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_DATASET_PREDICATE;
import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_SERVES_DATASET;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.openldes.server.admin.domain.validation.dcat.DcatNodeValidator;

public class CannotContainDatasetValidator implements DcatNodeValidator {
	private final List<CannotContainRule> rules;

	public CannotContainDatasetValidator() {
		rules = List.of(
				dcat -> !dcat.listSubjectsWithProperty(DCAT_SERVES_DATASET).hasNext(),
				dcat -> !dcat.listSubjectsWithProperty(RDF.type, DCAT_DATASET).hasNext(),
				dcat -> !dcat.listSubjectsWithProperty(DCAT_DATASET_PREDICATE).hasNext());
	}

	@Override
	public void validate(Model dcat) {
		boolean isValid = rules.stream()
				.allMatch(rule -> rule.evaluate(dcat));

		if (!isValid) {
			throw new IllegalArgumentException("Model cannot contain any kind of relation to dcat:Dataset.");
		}
	}
}
