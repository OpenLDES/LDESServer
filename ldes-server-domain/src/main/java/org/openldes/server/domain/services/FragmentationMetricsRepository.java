package org.openldes.server.domain.services;

import org.openldes.server.domain.model.FragmentationMetric;

import java.util.List;

public interface FragmentationMetricsRepository {
	List<FragmentationMetric> getBucketisedMemberCounts(String collectionName);
	List<FragmentationMetric> getPaginatedMemberCounts(String collectionName);
}
