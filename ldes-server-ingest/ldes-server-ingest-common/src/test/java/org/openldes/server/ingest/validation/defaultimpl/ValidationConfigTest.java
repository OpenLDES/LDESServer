package org.openldes.server.ingest.validation.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class ValidationConfigTest {

	@Test
	void test_configCanLoad() {
		ValidationConfig validationConfig = new ValidationConfig();
		var factory = assertDoesNotThrow(validationConfig::modelIngestValidatorFactory);
		assertDoesNotThrow(() -> validationConfig.ingestValidatorCollection(factory));
	}

}
