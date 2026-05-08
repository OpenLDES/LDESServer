package org.openldes.server.ingest.validation.defaultimpl.modelingestvalidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

class EmptyValidatorTest {

	@Test
	void whenValidate_shouldNeverThrowAnything() {
		assertDoesNotThrow(() -> new EmptyValidator().validate(null));
		assertDoesNotThrow(() -> new EmptyValidator().validate(ModelFactory.createDefaultModel()));
	}

}
