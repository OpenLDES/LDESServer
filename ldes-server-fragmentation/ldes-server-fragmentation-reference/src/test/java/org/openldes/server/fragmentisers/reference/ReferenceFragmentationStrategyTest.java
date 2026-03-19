package org.openldes.server.fragmentisers.reference;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.FragmentationStrategy;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.openldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import org.openldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.openldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static org.openldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator.FRAGMENT_KEY_REFERENCE_ROOT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReferenceFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final String TYPE_PARCEL = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel";
	private static final String TYPE_BUILDING = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Gebouw";
	private static final String TYPE_ADDRESS = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Adres";
	private Bucket parentBucket;
	private Bucket rootTileBucket;

	private ReferenceBucketiser referenceBucketiser;
	private ReferenceBucketCreator bucketCreator;
	private FragmentationStrategy decoratedFragmentationStrategy;
	private ReferenceFragmentationStrategy referenceFragmentationStrategy;

	@BeforeEach
	void setUp() {
		parentBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		rootTileBucket = parentBucket.createChild(new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT));

		referenceBucketiser = mock(ReferenceBucketiser.class);
		bucketCreator = mock(ReferenceBucketCreator.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_REFERENCE_ROOT))
				.thenReturn(rootTileBucket);
		referenceFragmentationStrategy = new ReferenceFragmentationStrategy(decoratedFragmentationStrategy,
				referenceBucketiser, bucketCreator, ObservationRegistry.create());
	}

	@Test
	void when_MemberIsAddedToBucket_ThenReferenceFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);
		when(referenceBucketiser.createReferences(member.getSubject(), member.getVersionModel()))
				.thenReturn(Set.of(TYPE_PARCEL, TYPE_BUILDING, TYPE_ADDRESS));
		Bucket referenceBucketOne = mockCreationReferenceBucket(TYPE_PARCEL);
		Bucket referenceBucketTwo = mockCreationReferenceBucket(TYPE_BUILDING);
		Bucket referenceBucketThree = mockCreationReferenceBucket(TYPE_ADDRESS);

		referenceFragmentationStrategy.addMemberToBucket(parentBucket, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(referenceBucketOne), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(referenceBucketTwo), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(referenceBucketThree), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private Bucket mockCreationReferenceBucket(String tile) {
		Bucket referenceBucket = parentBucket.createChild(new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, tile));
		when(bucketCreator.getOrCreateBucket(parentBucket, tile, rootTileBucket))
				.thenReturn(referenceBucket);
		return referenceBucket;
	}

}