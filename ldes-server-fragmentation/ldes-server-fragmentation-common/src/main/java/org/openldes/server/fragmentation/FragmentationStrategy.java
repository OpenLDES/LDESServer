package org.openldes.server.fragmentation;

import io.micrometer.observation.Observation;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;

public interface FragmentationStrategy {
	void addMemberToBucket(Bucket rootBucketOfView, FragmentationMember member, Observation parentObservation);
}
