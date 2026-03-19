package org.openldes.server.fragmentation.batch;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.FragmentationStrategyCollection;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketProcessors {
	@Bean
	@StepScope
	public ItemProcessor<FragmentationMember, Bucket> bucketProcessor(
			FragmentationStrategyCollection fragmentationStrategyCollection,
			@Value("#{jobParameters['collectionName']}") String collectionName,
			@Value("#{jobParameters['viewName']}") String viewName
	) {
		final String composedViewName = new ViewName(collectionName, viewName).asString();
		return item -> fragmentationStrategyCollection.getFragmentationStrategyExecutor(composedViewName)
				.map(executor -> executor.bucketise(item))
				.orElse(null);
	}
}
