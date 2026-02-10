package org.openldes.server.fetching.services;

import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.valueobjects.LdesFragmentRequest;

public interface TreeNodeFetcher {
	TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest);
}
