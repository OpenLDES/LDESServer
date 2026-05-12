package org.openldes.server.fragmentisers.geospatial.connected.relations;

import static org.openldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.GEOSPARQL_AS_WKT;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.TREE_GEOSPATIALLY_CONTAINS_RELATION;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.WGS_84;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.WKT_DATA_TYPE;

import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.exceptions.MissingFragmentValueException;
import org.openldes.server.fragmentation.relations.RelationsAttributer;
import org.openldes.server.fragmentation.valueobjects.TreeRelation;
import org.openldes.server.fragmentisers.geospatial.connected.BoundingBox;
import org.openldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter;
import org.openldes.server.fragmentisers.geospatial.converter.TileConverter;
import org.openldes.server.fragmentisers.geospatial.model.Tile;

public class TileBucketRelationsAttributer implements RelationsAttributer {

	public Bucket addRelationsFromRootToBottom(Bucket rootBucket, Bucket tileBucket) {
		boolean isDefaultBucket = tileBucket.getValueForKey(FRAGMENT_KEY_TILE).orElse("").equals(DEFAULT_BUCKET_STRING);
		TreeRelation treeRelation = isDefaultBucket ? TreeRelation.generic() : createGeospatialRelationToParent(tileBucket);
		return rootBucket.addChildBucket(tileBucket.withRelations(treeRelation));
	}

	private TreeRelation createGeospatialRelationToParent(Bucket childBucket) {
		final String treeValue = WGS_84 + " " + getWKT(childBucket);
		return new TreeRelation(
				TREE_GEOSPATIALLY_CONTAINS_RELATION,
				treeValue,
				WKT_DATA_TYPE,
				GEOSPARQL_AS_WKT
		);
	}

	private String getWKT(Bucket currentBucket) {
		String fragmentWKT = currentBucket.getValueForKey(FRAGMENT_KEY_TILE).orElseThrow(
				() -> new MissingFragmentValueException(currentBucket.getBucketDescriptorAsString(), FRAGMENT_KEY_TILE));
		Tile currentTile = TileConverter.fromString(fragmentWKT);
		BoundingBox currentBoundingBox = new BoundingBox(currentTile);
		return BoundingBoxConverter.toWkt(currentBoundingBox);
	}
}
