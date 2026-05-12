package org.openldes.server.fragmentisers.geospatial.fragments;

import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.openldes.server.fragmentisers.geospatial.connected.relations.TileBucketRelationsAttributer;

public class GeospatialBucketCreator {

	private final TileBucketRelationsAttributer tileBucketRelationsAttributer;

	public GeospatialBucketCreator(TileBucketRelationsAttributer tileBucketRelationsAttributer) {
		this.tileBucketRelationsAttributer = tileBucketRelationsAttributer;
	}

	public Bucket createTileBucket(Bucket parentBucket, String tile, Bucket rootTileFragment) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		final Bucket childBucket = parentBucket.createChild(childDescriptorPair);
		return tileBucketRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, childBucket);
	}

	public Bucket createRootBucket(Bucket parentBucket, String tile) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		return parentBucket.createChild(childDescriptorPair);
	}
}
