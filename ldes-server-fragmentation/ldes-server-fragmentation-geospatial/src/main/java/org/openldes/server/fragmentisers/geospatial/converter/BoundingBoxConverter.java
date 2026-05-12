package org.openldes.server.fragmentisers.geospatial.converter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.openldes.server.fragmentisers.geospatial.connected.BoundingBox;

public class BoundingBoxConverter {

	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	private BoundingBoxConverter() {
	}

	public static Geometry toPolygon(BoundingBox boundingBox) {
		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(boundingBox.getEast(), boundingBox.getNorth()),
				new Coordinate(boundingBox.getEast(), boundingBox.getSouth()),
				new Coordinate(boundingBox.getWest(), boundingBox.getSouth()),
				new Coordinate(boundingBox.getWest(), boundingBox.getNorth()),
				new Coordinate(boundingBox.getEast(), boundingBox.getNorth()) };
		return GEOMETRY_FACTORY.createPolygon(coordinates);
	}

	public static String toWkt(BoundingBox boundingBox) {
		return toPolygon(boundingBox).toText();
	}

}
