package org.openldes.server.domain.services;

import java.util.List;
import org.openldes.server.domain.model.FragmentationMetric;

public interface FragmentationMetricsRepository {
	List<FragmentationMetric> getBucketisedMemberCounts(String collectionName);
	List<FragmentationMetric> getPaginatedMemberCounts(String collectionName);
}
