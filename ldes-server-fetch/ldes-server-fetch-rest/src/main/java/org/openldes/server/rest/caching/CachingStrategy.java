package org.openldes.server.rest.caching;

import org.openldes.server.fetching.entities.TreeNode;

public interface CachingStrategy {

	String generateCacheIdentifier(String collectionName, String language);

	String generateCacheIdentifier(TreeNode treeNode, String language);
}
