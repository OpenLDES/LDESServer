package org.openldes.server.fetch.postgres.mapper;

import java.util.List;
import org.openldes.server.fetch.postgres.projection.TreeNodeProjection;
import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;

public class TreeNodeMapper {
	private TreeNodeMapper() {
	}

	public static TreeNode fromProjection(TreeNodeProjection page, List<Member> members) {
		return new TreeNode(
				page.getPartialUrl(),
				page.isImmutable(),
				page.isView(),
				page.getRelations().stream().map(TreeRelationMapper::fromRelation).toList(),
				members,
				page.getBucket().getView().getEventStream().getName(),
				page.getNextUpdateTs()
		);
	}

}
