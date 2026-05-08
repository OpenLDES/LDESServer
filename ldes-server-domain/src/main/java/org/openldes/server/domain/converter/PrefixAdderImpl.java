package org.openldes.server.domain.converter;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.domain.collections.Prefixes;
import org.springframework.stereotype.Component;

@Component
public class PrefixAdderImpl implements PrefixAdder {
	private final List<Prefixes> prefixes;

	public PrefixAdderImpl(List<Prefixes> prefixes) {
		this.prefixes = prefixes;
	}

	@Override
	public Model addPrefixesToModel(Model model) {
		prefixes.stream().map(Prefixes::getPrefixes).forEach(model::setNsPrefixes);
		return model;
	}
}
