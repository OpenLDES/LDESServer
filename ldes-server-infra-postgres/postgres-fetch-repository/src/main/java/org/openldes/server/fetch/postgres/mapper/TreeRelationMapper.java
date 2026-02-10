package org.openldes.server.fetch.postgres.mapper;

import org.openldes.server.fetch.postgres.entity.FetchPageRelationEntity;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.openldes.server.fetching.valueobjects.TreeRelation;

public class TreeRelationMapper {
	private TreeRelationMapper() {}

	public static TreeRelation fromRelation(FetchPageRelationEntity pageRelation) {
		return new TreeRelation(
				pageRelation.getTreePath(),
				LdesFragmentIdentifier.fromFragmentId(pageRelation.getToPage().getPartialUrl()),
				pageRelation.getTreeValue(),
				pageRelation.getTreeValueType(),
				pageRelation.getTreeRelationType()
		);
	}
}
