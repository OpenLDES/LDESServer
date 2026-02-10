package org.openldes.server.fragmentation;

import org.openldes.server.domain.model.ConfigProperties;
import org.springframework.context.ApplicationContext;

public interface FragmentationStrategyWrapper {
	FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
	                                                FragmentationStrategy fragmentationStrategy,
	                                                ConfigProperties fragmentationProperties);
}
