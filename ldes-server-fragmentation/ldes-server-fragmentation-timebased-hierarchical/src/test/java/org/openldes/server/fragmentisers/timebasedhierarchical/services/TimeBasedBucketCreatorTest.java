package org.openldes.server.fragmentisers.timebasedhierarchical.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.openldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.openldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;

class TimeBasedBucketCreatorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timePair = new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023");
	private Bucket parent;
	private Bucket root;
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			Granularity.MONTH);

	private TimeBasedRelationsAttributer relationsAttributer;
	private TimeBasedBucketCreator bucketCreator;

	@BeforeEach
	void setUp() {
		relationsAttributer = mock(TimeBasedRelationsAttributer.class);
		bucketCreator = new TimeBasedBucketCreator(relationsAttributer);
		parent = new Bucket(new BucketDescriptor(List.of(timePair)), VIEW_NAME);
		root = Bucket.createRootBucketForView(VIEW_NAME);
	}

	@Test
	void test_GetOrCreateInBetweenBucket() {
		final BucketDescriptor childDescriptor = new BucketDescriptor(List.of(timePair, new BucketDescriptorPair(Granularity.MONTH.getValue(), "01")));
		when(relationsAttributer.addInBetweenRelation(parent, new Bucket(childDescriptor, VIEW_NAME)))
				.thenReturn(new Bucket(childDescriptor, VIEW_NAME));

		Bucket child = bucketCreator.createBucket(parent, TIME, Granularity.MONTH);

		verify(relationsAttributer).addInBetweenRelation(eq(parent), any());
		assertThat(child.getBucketDescriptor()).isEqualTo(childDescriptor);
	}

	@Test
	void test_GetOrCreateDefaultBucket() {
		BucketDescriptor childDescriptor = new BucketDescriptor(
				List.of(new BucketDescriptorPair(Granularity.YEAR.getValue(), DEFAULT_BUCKET_STRING)));
		when(relationsAttributer.addDefaultRelation(root, new Bucket(childDescriptor, VIEW_NAME)))
				.thenReturn(new Bucket(childDescriptor, VIEW_NAME));

		Bucket child = bucketCreator.createBucket(root, DEFAULT_BUCKET_STRING, Granularity.YEAR);

		verify(relationsAttributer).addDefaultRelation(eq(root), any());
		assertThat(child.getBucketDescriptor()).isEqualTo(childDescriptor);
	}
}
