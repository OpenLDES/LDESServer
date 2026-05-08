package org.openldes.server.fragmentisers.timebasedhierarchical;

import static org.openldes.server.fragmentisers.timebasedhierarchical.HierarchicalTimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("org.openldes.server")
public class HierarchicalTimeBasedFragmentationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(TIMEBASED_FRAGMENTATION_HIERARCHICAL)
	public HierarchicalTimeBasedFragmentationStrategyWrapper timeBasedFragmentationStrategyWrapper() {
		return new HierarchicalTimeBasedFragmentationStrategyWrapper();
	}

}
