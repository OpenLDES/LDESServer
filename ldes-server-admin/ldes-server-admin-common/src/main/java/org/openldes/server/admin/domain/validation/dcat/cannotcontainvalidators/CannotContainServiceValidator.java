package org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators;

import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_DATA_SERVICE;
import static org.openldes.server.admin.domain.validation.dcat.DcatValidator.DCAT_DATA_SERVICE_PREDICATE;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.openldes.server.admin.domain.validation.dcat.DcatNodeValidator;

public class CannotContainServiceValidator implements DcatNodeValidator {
	private final List<CannotContainRule> rules;

	public CannotContainServiceValidator() {
		rules = List.of(
				dcat -> !dcat.listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).hasNext(),
				dcat -> !dcat.listSubjectsWithProperty(DCAT_DATA_SERVICE_PREDICATE).hasNext());
	}

	@Override
	public void validate(Model dcat) {
		boolean isValid = rules.stream()
				.allMatch(rule -> rule.evaluate(dcat));

		if (!isValid) {
			throw new IllegalArgumentException("Model cannot contain any kind of relation to dcat:DataService.");
		}
	}
}
