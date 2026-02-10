package org.openldes.server.fragmentation.factory;

import org.openldes.server.domain.model.ViewSpecification;
import org.openldes.server.fragmentation.FragmentationStrategy;

public interface FragmentationStrategyCreator {
	FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification);
}
