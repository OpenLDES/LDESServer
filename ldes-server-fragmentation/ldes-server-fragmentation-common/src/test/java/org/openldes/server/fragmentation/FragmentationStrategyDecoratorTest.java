package org.openldes.server.fragmentation;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.FragmentationMember;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyDecoratorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	@Mock
	private FragmentationStrategy fragmentationStrategy;
	@InjectMocks
	private FragmentationStrategyDecoratorTestImpl fragmentationStrategyDecorator;


	@Test
	void when_DecoratorAddsMemberToBucket_WrappedFragmentationStrategyIsCalled() {
		Bucket parentBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		FragmentationMember member = mock(FragmentationMember.class);
		Observation span = mock(Observation.class);

		fragmentationStrategyDecorator.addMemberToBucket(parentBucket, member, span);

		verify(fragmentationStrategy).addMemberToBucket(parentBucket, member, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends FragmentationStrategyDecorator {
		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy) {
			super(fragmentationStrategy);
		}
	}
}
