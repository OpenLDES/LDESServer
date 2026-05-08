package org.openldes.server.fragmentisers.geospatial.connected.relations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.openldes.server.fragmentation.valueobjects.TreeRelation;

@ExtendWith(MockitoExtension.class)
class TileBucketRelationsAttributerTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Bucket PARENT_BUCKET = new Bucket(BucketDescriptor.empty(), VIEW_NAME);

	private TileBucketRelationsAttributer tileBucketRelationsAttributer;


	@BeforeEach
	void setUp() {
		tileBucketRelationsAttributer = new TileBucketRelationsAttributer();
	}

	@Test
	void when_TileFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
		Bucket rootBucket = createTileBucket("0/0/0");
		Bucket tileBucket = createTileBucket("1/1/1");
		TreeRelation treeRelation = new TreeRelation(
				"https://w3id.org/tree#GeospatiallyContainsRelation",
				"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POLYGON ((180 0, 180 -85.0511287798066, 0 -85.0511287798066, 0 0, 180 0))",
				"http://www.opengis.net/ont/geosparql#wktLiteral",
				"http://www.opengis.net/ont/geosparql#asWKT"
				);

		tileBucketRelationsAttributer.addRelationsFromRootToBottom(rootBucket, tileBucket);

		assertThat(rootBucket.getChildren())
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(tileBucket.withRelations(treeRelation));
	}

	@Test
	void when_DefaultFragmentIsCreated_RelationsBetweenRootAndCreatedFragmentIsAdded() {
		Bucket rootBucket = createTileBucket("0/0/0");
		Bucket tileBucket = createTileBucket(DEFAULT_BUCKET_STRING);

		tileBucketRelationsAttributer.addRelationsFromRootToBottom(rootBucket, tileBucket);

		assertThat(rootBucket.getChildren())
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(tileBucket.withGenericRelation());
	}

	private Bucket createTileBucket(String tile) {
		return PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile));
	}

}
