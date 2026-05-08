package org.openldes.server.fragmentisers.geospatial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.model.ConfigProperties;
import org.openldes.server.fragmentation.FragmentationStrategy;
import org.springframework.context.ApplicationContext;

class GeospatialFragmentationStrategyWrapperTest {

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private GeospatialFragmentationStrategyWrapper geospatialFragmentationUpdater;

	@BeforeEach
	void setUp() {
		geospatialFragmentationUpdater = new GeospatialFragmentationStrategyWrapper();
	}

	@Test
	void when_FragmentationStrategyIsUpdated_GeospatialFragmentationStrategyIsReturned() {
		ConfigProperties properties = new ConfigProperties(
				Map.of("maxZoom", "15", "fragmentationPath", "http://www.opengis.net/ont/geosparql#asWKT"));

		FragmentationStrategy decoratedFragmentationStrategy = geospatialFragmentationUpdater
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);

		assertThat(decoratedFragmentationStrategy).isInstanceOf(GeospatialFragmentationStrategy.class);
	}
}
