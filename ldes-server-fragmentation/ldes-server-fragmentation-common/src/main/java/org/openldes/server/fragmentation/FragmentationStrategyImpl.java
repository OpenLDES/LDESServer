package org.openldes.server.fragmentation;

import io.micrometer.observation.Observation;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	@Override
	public void addMemberToBucket(Bucket rootBucketOfView, FragmentationMember member, Observation parentObservation) {
		rootBucketOfView.assignMember(member.getMemberId());
	}
}
