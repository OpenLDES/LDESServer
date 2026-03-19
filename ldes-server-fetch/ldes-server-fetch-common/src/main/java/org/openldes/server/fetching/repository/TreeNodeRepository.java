package org.openldes.server.fetching.repository;

import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;

import java.util.Optional;

public interface TreeNodeRepository {
	Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier);
	Optional<TreeNode> findTreeNodeWithoutMembers(LdesFragmentIdentifier fragmentIdentifier);
}
