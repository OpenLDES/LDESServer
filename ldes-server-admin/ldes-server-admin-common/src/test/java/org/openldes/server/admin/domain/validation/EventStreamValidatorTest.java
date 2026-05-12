package org.openldes.server.admin.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.exceptions.ShaclValidationException;
import org.openldes.server.domain.model.EventStream;

class EventStreamValidatorTest {

	private final ModelValidator validator = new ShaclValidator("shacl/validation/eventstreamShaclShape.ttl");

	@Test
	void test_support() {
		Model model = ModelFactory.createDefaultModel();

		assertThat(validator.supports(model.getClass())).isTrue();
		assertThat(validator.supports(Model.class)).isTrue();

		assertThat(validator.supports(EventStream.class)).isFalse();
		assertThat(validator.supports(Integer.class)).isFalse();
	}

	@Test
	void when_validLdesProvided_then_returnValid() throws URISyntaxException {
		Model model = readModelFromFile("eventstream/streams/valid-ldes.ttl");

		assertThatNoException().isThrownBy(() -> validator.validate(model));
	}

	@Test
	void when_invalidLdesProvided_then_returnInvalid() throws URISyntaxException {
		Model model = readModelFromFile("shacl/invalid-shape.ttl");

		assertThatThrownBy(() -> validator.validate(model)).isInstanceOf(ShaclValidationException.class);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}
