package org.openldes.server.fragmentisers.geospatial;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class GeospatialFragmentationStrategyAutoConfigurationTest {

	@Test
	void triggerEspgDatabaseInitializationOnStartupShouldNotFail() {
		assertDoesNotThrow(() -> new GeospatialFragmentationStrategyAutoConfiguration()
				.triggerEspgDatabaseInitializationOnStartup());
	}

}
