package org.openldes.server.fragmentisers.timebasedhierarchical.services;

import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.openldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.openldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.openldes.server.fragmentation.FragmentationJobScheduler.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static org.openldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static org.openldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static org.openldes.server.fragmentisers.timebasedhierarchical.HierarchicalTimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

public class TimeBasedBucketCreator {
	private final TimeBasedRelationsAttributer relationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedBucketCreator.class);

	public TimeBasedBucketCreator(TimeBasedRelationsAttributer relationsAttributer) {
		this.relationsAttributer = relationsAttributer;
	}

	public Bucket createBucket(Bucket parentBucket,
	                           FragmentationTimestamp fragmentationTimestamp,
	                           Granularity granularity) {
		return createBucket(parentBucket, fragmentationTimestamp.getTimeValueForGranularity(granularity), granularity);
	}

	public Bucket createBucket(Bucket parentBucket, String timeValue, Granularity granularity) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(granularity.getValue(), timeValue);
		final Bucket childBucket = parentBucket.createChild(childDescriptorPair);
		logBucketisation(parentBucket, childBucket);
		return addRelationToParent(parentBucket, childBucket);
	}

	private Bucket addRelationToParent(Bucket parentBucket, Bucket childBucket) {
		if (isDefaultBucket(childBucket)) {
			return relationsAttributer.addDefaultRelation(parentBucket, childBucket);
		} else {
			return relationsAttributer.addInBetweenRelation(parentBucket, childBucket);
		}
	}


	private boolean isDefaultBucket(Bucket bucket) {
		return bucket.getValueForKey(Granularity.YEAR.getValue()).orElse("").equals(DEFAULT_BUCKET_STRING);
	}

	private void logBucketisation(Bucket parentBucket, Bucket child) {
		String viewName = parentBucket.getViewName().asString();
		Metrics
				.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, TIMEBASED_FRAGMENTATION_HIERARCHICAL)
				.increment();
		LOGGER.debug("Timebased fragment created with id: {}", child.getBucketDescriptorAsString());
	}


}
