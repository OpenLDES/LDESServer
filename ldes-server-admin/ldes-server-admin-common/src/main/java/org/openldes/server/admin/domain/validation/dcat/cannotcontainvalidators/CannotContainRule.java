package org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface CannotContainRule {
	boolean evaluate(Model model);
}
