package org.openldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;

public interface PrefixAdder {
	Model addPrefixesToModel(Model model);
}
