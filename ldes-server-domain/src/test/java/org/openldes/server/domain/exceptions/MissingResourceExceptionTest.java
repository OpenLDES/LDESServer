package org.openldes.server.domain.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MissingResourceExceptionTest {
	@Test
	void test_CorrectErrorMessage() {
		assertThat(new MissingResourceException("resource", "resource-id"))
				.hasMessage("Resource of type: resource with id: resource-id could not be found.");
	}
}
