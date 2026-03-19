package org.openldes.server.fragmentation.valueobjects;

import org.openldes.server.domain.constants.RdfConstants;

public record TreeRelation(String treeRelationType, String treeValue, String treeValueType, String treePath) {

	public static TreeRelation generic() {
		return new TreeRelation(RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}

}
