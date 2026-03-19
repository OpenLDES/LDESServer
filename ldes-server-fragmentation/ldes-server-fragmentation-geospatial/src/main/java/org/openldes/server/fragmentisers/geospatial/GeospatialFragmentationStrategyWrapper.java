package org.openldes.server.fragmentisers.geospatial;

import org.openldes.server.domain.model.ConfigProperties;
import org.openldes.server.fragmentation.FragmentationStrategy;
import org.openldes.server.fragmentation.FragmentationStrategyWrapper;
import org.openldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import org.openldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.openldes.server.fragmentisers.geospatial.connected.relations.TileBucketRelationsAttributer;
import org.openldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static org.openldes.server.fragmentisers.geospatial.config.GeospatialProperties.*;

public class GeospatialFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		TileBucketRelationsAttributer tileBucketRelationsAttributer = new TileBucketRelationsAttributer();

		GeospatialConfig geospatialConfig = createGeospatialConfig(fragmentationProperties);
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig);
		GeospatialBucketCreator geospatialBucketCreator = new GeospatialBucketCreator(tileBucketRelationsAttributer);

		return new GeospatialFragmentationStrategy(fragmentationStrategy,
				geospatialBucketiser, geospatialBucketCreator, observationRegistry);
	}

	private GeospatialConfig createGeospatialConfig(ConfigProperties properties) {
		return new GeospatialConfig(
				properties.getOrDefault(FRAGMENTER_SUBJECT_FILTER, ".*"),
				properties.get(FRAGMENTATION_PROPERTY),
				Integer.parseInt(properties.get(MAX_ZOOM)));
	}

}
