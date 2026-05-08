package org.openldes.server.admin.postgres.dcatserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import org.openldes.server.admin.postgres.dcatserver.entity.DcatCatalogEntity;
import org.openldes.server.domain.constants.RdfConstants;

class DcatCatalogEntityConverterTest {
	private static final String ID = "id";
	private static DcatServer dcatServer;
	private final DcatCatalogEntityConverter converter = new DcatCatalogEntityConverter();

	@BeforeAll
	static void beforeAll() {
		Resource subject = ResourceFactory.createResource("http://localhost:8080/dcat/id");
		Resource object = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#", "Catalog");
		Statement statement = ResourceFactory.createStatement(subject, RdfConstants.RDF_SYNTAX_TYPE, object);
		Model dcat = ModelFactory.createDefaultModel().add(statement);
		dcatServer = new DcatServer(ID, dcat);
	}

	@Test
	void test_conversionFromAndToDomain() {
		final DcatCatalogEntity entity = converter.fromDcatServer(dcatServer);
		final DcatServer converted = converter.toDcatServer(entity);

		assertEquals(dcatServer, converted);
		assertEquals(dcatServer.getId(), entity.getId());
		assertTrue(entity.getDcat().contains("<http://localhost:8080/dcat/id>"));
	}
}
