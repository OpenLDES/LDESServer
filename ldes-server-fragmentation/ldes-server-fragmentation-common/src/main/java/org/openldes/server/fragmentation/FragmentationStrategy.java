package org.openldes.server.fragmentation;

import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;

public interface FragmentationStrategy {
	void addMemberToBucket(Bucket rootBucketOfView, FragmentationMember member, Observation parentObservation);
}