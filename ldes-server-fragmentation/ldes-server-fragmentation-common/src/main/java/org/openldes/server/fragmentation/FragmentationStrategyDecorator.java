package org.openldes.server.fragmentation;

import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {
	private final FragmentationStrategy fragmentationStrategy;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy) {
		this.fragmentationStrategy = fragmentationStrategy;
	}

	@Override
	public void addMemberToBucket(Bucket parentBucket, FragmentationMember member, Observation parentObservation) {
		fragmentationStrategy.addMemberToBucket(parentBucket, member, parentObservation);
	}

}
