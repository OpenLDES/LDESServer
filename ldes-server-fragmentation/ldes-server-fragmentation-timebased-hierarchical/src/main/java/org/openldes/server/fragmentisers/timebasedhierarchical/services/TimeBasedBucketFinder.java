package org.openldes.server.fragmentisers.timebasedhierarchical.services;

import static org.openldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import org.openldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.openldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;

public class TimeBasedBucketFinder {
	private final TimeBasedBucketCreator bucketCreator;
	private final TimeBasedConfig config;

	public TimeBasedBucketFinder(TimeBasedBucketCreator bucketCreator, TimeBasedConfig config) {
		this.bucketCreator = bucketCreator;
		this.config = config;
	}

	public Bucket getLowestBucket(Bucket parentFragment, FragmentationTimestamp fragmentationTimestamp,
	                              Granularity granularity) {
		if (isLowest(parentFragment)) {
			return parentFragment;
		}
		return getLowestBucket(
				bucketCreator.createBucket(parentFragment, fragmentationTimestamp, granularity),
				fragmentationTimestamp, granularity.getChild());
	}

	public Bucket getDefaultFragment(Bucket rootFragment) {
		return bucketCreator.createBucket(rootFragment, DEFAULT_BUCKET_STRING, Granularity.YEAR);
	}

	private boolean isLowest(Bucket bucket) {
		return bucket.getValueForKey(config.getMaxGranularity().getValue()).isPresent();
	}
}
