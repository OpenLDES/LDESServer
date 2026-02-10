package org.openldes.server.rest.treenode.services;

import org.openldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeConverter {
	Model toModel(final TreeNode treeNode);
}
