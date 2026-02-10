package org.openldes.server.fragmentation.factory;

import org.openldes.server.domain.model.FragmentationConfig;
import org.openldes.server.domain.model.ViewSpecification;
import org.openldes.server.fragmentation.FragmentationStrategy;
import org.openldes.server.fragmentation.FragmentationStrategyImpl;
import org.openldes.server.fragmentation.FragmentationStrategyWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentationStrategyCreatorImpl implements FragmentationStrategyCreator {
	private final ApplicationContext applicationContext;
	private final RootBucketCreator rootBucketCreator;

	public FragmentationStrategyCreatorImpl(ApplicationContext applicationContext, RootBucketCreator rootBucketCreator) {
		this.applicationContext = applicationContext;
		this.rootBucketCreator = rootBucketCreator;
	}

	public FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification) {
		rootBucketCreator.createRootBucketForView(viewSpecification.getName());
		FragmentationStrategy fragmentationStrategy = new FragmentationStrategyImpl();
		if (viewSpecification.getFragmentations() != null) {
			fragmentationStrategy = wrapFragmentationStrategy(viewSpecification.getFragmentations(),
					fragmentationStrategy);
		}
		return fragmentationStrategy;
	}

	private FragmentationStrategy wrapFragmentationStrategy(List<FragmentationConfig> fragmentationConfigs,
	                                                        FragmentationStrategy fragmentationStrategy) {
		for (int i = fragmentationConfigs.size() - 1; i >= 0; i--) {
			FragmentationConfig currentFragmentationConfig = fragmentationConfigs.get(i);
			fragmentationStrategy = wrapFragmentationStrategyUsingFragmentationConfig(fragmentationStrategy,
					currentFragmentationConfig);
		}
		return fragmentationStrategy;
	}

	private FragmentationStrategy wrapFragmentationStrategyUsingFragmentationConfig(
			FragmentationStrategy fragmentationStrategy, FragmentationConfig currentFragmentationConfig) {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = (FragmentationStrategyWrapper) applicationContext
				.getBean(currentFragmentationConfig.getName());
		fragmentationStrategy = fragmentationStrategyWrapper.wrapFragmentationStrategy(applicationContext,
				fragmentationStrategy, currentFragmentationConfig.getProperties());
		return fragmentationStrategy;
	}
}
