package org.openldes.server.fragmentisers.reference;

import org.openldes.server.domain.model.ConfigProperties;
import org.openldes.server.fragmentation.FragmentationStrategy;
import org.openldes.server.fragmentation.FragmentationStrategyWrapper;
import org.openldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import org.openldes.server.fragmentisers.reference.config.ReferenceConfig;
import org.openldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator;
import org.openldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static org.openldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;

public class ReferenceFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public static final String FRAGMENTATION_PATH = "fragmentationPath";
	public static final String DEFAULT_FRAGMENTATION_PATH = RDF_SYNTAX_TYPE.getURI();

	public static final String FRAGMENTATION_KEY = "fragmentationKey";
	public static final String DEFAULT_FRAGMENTATION_KEY = "reference";

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties properties) {
		final var fragmentationPath = properties.getOrDefault(FRAGMENTATION_PATH, DEFAULT_FRAGMENTATION_PATH);
		final var observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		final var referenceConfig = new ReferenceConfig(fragmentationPath);
		final var referenceBucketiser = new ReferenceBucketiser(referenceConfig);
		final var fragmentationKey = properties.getOrDefault(FRAGMENTATION_KEY, DEFAULT_FRAGMENTATION_KEY);
		final var relationsAttributer = new ReferenceFragmentRelationsAttributer(fragmentationPath, fragmentationKey);

		final var referenceBucketCreator = new ReferenceBucketCreator(relationsAttributer, fragmentationKey);
		return new ReferenceFragmentationStrategy(
				fragmentationStrategy,
				referenceBucketiser,
				referenceBucketCreator,
				observationRegistry
		);
	}

}
