package org.openldes.server.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ShaclValidationExceptionTest {
	@Test
	void test_CorrectErrorMessage() {
		assertThat(new ShaclValidationException("msg", null)).hasMessage("Shacl validation failed: \n\nmsg");
	}
}
