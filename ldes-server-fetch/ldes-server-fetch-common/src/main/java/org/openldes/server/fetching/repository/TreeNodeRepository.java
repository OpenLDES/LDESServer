package org.openldes.server.fetching.repository;

import java.util.Optional;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;

public interface TreeNodeRepository {
	Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier);
	Optional<TreeNode> findTreeNodeWithoutMembers(LdesFragmentIdentifier fragmentIdentifier);
}
