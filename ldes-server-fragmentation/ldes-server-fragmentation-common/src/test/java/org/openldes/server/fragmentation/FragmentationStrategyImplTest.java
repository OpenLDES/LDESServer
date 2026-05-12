package org.openldes.server.fragmentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.observation.Observation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.BucketisedMember;
import org.openldes.server.fragmentation.entities.FragmentationMember;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;

class FragmentationStrategyImplTest {
	private static final long BUCKET_ID = 2L;
	private static final long MEMBER_ID = 1L;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl();

	@Test
	void when_memberIsAddedToBucket_FragmentationStrategyImplAddsMemberToBucket() {
		Bucket bucket = new Bucket(BUCKET_ID, BucketDescriptor.empty(), VIEW_NAME, List.of(), 0);
		FragmentationMember member = mock(FragmentationMember.class);
		BucketisedMember expected = new BucketisedMember(BUCKET_ID, MEMBER_ID);
		when(member.getMemberId()).thenReturn(MEMBER_ID);

		fragmentationStrategy.addMemberToBucket(bucket, member, mock(Observation.class));

		assertThat(bucket.getMember()).contains(expected);
	}
}
