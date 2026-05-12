package org.openldes.server.domain.converter;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

public class RetentionModelSerializer {
	private static final Lang dbSavedLang = Lang.NQUADS;

	public List<String> serialize(List<Model> models) {
		return models
				.stream()
				.map(retentionModel -> RdfModelConverter.toString(retentionModel, dbSavedLang))
				.toList();
	}

	public List<Model> deserialize(List<String> retentionPolicies) {
		return retentionPolicies
				.stream()
				.map(retentionModel -> RDFParser.create().fromString(retentionModel).lang(dbSavedLang).toModel())
				.toList();
	}
}
