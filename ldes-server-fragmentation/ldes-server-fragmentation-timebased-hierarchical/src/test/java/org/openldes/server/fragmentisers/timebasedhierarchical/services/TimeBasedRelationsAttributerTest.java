package org.openldes.server.fragmentisers.timebasedhierarchical.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.TREE_GTE_RELATION;
import static org.openldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.TREE_LT_RELATION;
import static org.openldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.XSD_DATETIME;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.openldes.server.fragmentation.valueobjects.TreeRelation;
import org.openldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import org.openldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;

class TimeBasedRelationsAttributerTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timePair = new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023");
	private static final BucketDescriptorPair monthPair = new BucketDescriptorPair(Granularity.MONTH.getValue(), "02");
	private Bucket parentBucket;
	private TimeBasedRelationsAttributer relationsAttributer;
	private TimeBasedConfig config;

	@BeforeEach
	void setUp() {
		config = new TimeBasedConfig(".*", "", Granularity.SECOND);
		relationsAttributer = new TimeBasedRelationsAttributer(config);
		parentBucket = new Bucket(BucketDescriptor.of(timePair), VIEW_NAME);
	}

	@Test
	void when_RelationNotPresent_ThenRelationIsAdded_NextUpdateTsIsNotSet_ChildrenStayMutable() {
		Bucket child = parentBucket.createChild(monthPair);

		TreeRelation gteRelation = new TreeRelation(
				TREE_GTE_RELATION,
				LocalDateTime.of(2023,2,1,0,0).toString(),
				XSD_DATETIME,
				config.getFragmentationPath());
		TreeRelation ltRelation = new TreeRelation(
				TREE_LT_RELATION,
				LocalDateTime.of(2023,3,1,0,0).toString(),
				XSD_DATETIME,
				config.getFragmentationPath());

		relationsAttributer.addInBetweenRelation(parentBucket, child);

		assertThat(parentBucket.getChildren())
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(
						child.withRelations(gteRelation, ltRelation)
				);

	}

	@Test
	void when_RelationNotPresent_Then_AddDefaultRelation() {
		Bucket child = parentBucket.createChild(monthPair);

		relationsAttributer.addDefaultRelation(parentBucket, child);

		assertThat(parentBucket.getChildren())
				.containsExactly(child.withGenericRelation());
	}

}
