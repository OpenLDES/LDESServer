package org.openldes.server.admin.postgres.dcatserver.service;

import static org.openldes.server.admin.postgres.PostgresAdminConstants.SERIALISATION_LANG;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.openldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import org.openldes.server.admin.postgres.dcatserver.entity.DcatCatalogEntity;
import org.springframework.stereotype.Component;

@Component
public class DcatCatalogEntityConverter {
	public DcatCatalogEntity fromDcatServer(DcatServer dcatServer) {
		final String dcatString = RDFWriter.source(dcatServer.getDcat())
				.lang(SERIALISATION_LANG)
				.asString();
		return new DcatCatalogEntity(dcatServer.getId(), dcatString);
	}

	public DcatServer toDcatServer(DcatCatalogEntity dcatCatalogEntity) {
		final Model dcat = RDFParserBuilder.create()
				.fromString(dcatCatalogEntity.getDcat())
				.lang(SERIALISATION_LANG)
				.toModel();
		return new DcatServer(dcatCatalogEntity.getId(), dcat);
	}
}
