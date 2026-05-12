package org.openldes.server.fragmentation;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.factory.RootBucketCreatorImpl;
import org.openldes.server.fragmentation.repository.BucketRepository;

@ExtendWith(MockitoExtension.class)
class RootBucketCreatorImplTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "mobility-hindrances");
	@Mock
	private BucketRepository bucketRepository;
	@InjectMocks
	private RootBucketCreatorImpl rootBucketCreator;

	@Test
	void when_RootFragmentDoesNotExist_ItIsCreatedAndSaved() {
		rootBucketCreator.createRootBucketForView(VIEW_NAME);

		InOrder inOrder = inOrder(bucketRepository);
		inOrder.verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
		inOrder.verify(bucketRepository).insertRootBucket(Bucket.createRootBucketForView(VIEW_NAME));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_RootFragmentExists_NothingHappens() {
		when(bucketRepository.retrieveRootBucket(VIEW_NAME)).thenReturn(Optional.of(mock(Bucket.class)));

		rootBucketCreator.createRootBucketForView(VIEW_NAME);

		InOrder inOrder = inOrder(bucketRepository);
		inOrder.verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
		inOrder.verifyNoMoreInteractions();
	}
}
