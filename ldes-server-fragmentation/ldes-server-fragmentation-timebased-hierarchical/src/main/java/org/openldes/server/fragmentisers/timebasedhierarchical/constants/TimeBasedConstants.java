package org.openldes.server.fragmentisers.timebasedhierarchical.constants;

import static org.openldes.server.domain.constants.RdfConstants.TREE;

public class TimeBasedConstants {

	private TimeBasedConstants() {
	}

	private static final String XSD = "http://www.w3.org/2001/XMLSchema#";
	public static final String TREE_GTE_RELATION = TREE + "GreaterThanOrEqualToRelation";
	public static final String TREE_LT_RELATION = TREE + "LessThanRelation";
	public static final String XSD_DATETIME = XSD + "dateTime";

}
