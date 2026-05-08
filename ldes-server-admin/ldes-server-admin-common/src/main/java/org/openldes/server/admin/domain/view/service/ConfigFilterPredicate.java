package org.openldes.server.admin.domain.view.service;

import java.util.function.Predicate;
import org.apache.jena.rdf.model.Statement;

public class ConfigFilterPredicate implements Predicate<Statement> {

	private final String type;

	public ConfigFilterPredicate(String type) {
		this.type = type;
	}

	@Override
	public boolean test(Statement statement) {
		return statement.getPredicate().toString().equals(type);
	}
}
