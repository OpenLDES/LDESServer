package org.openldes.server.rest.treenode.services;

import org.apache.jena.rdf.model.Model;
import org.openldes.server.fetching.entities.TreeNode;

public interface TreeNodeConverter {
	Model toModel(final TreeNode treeNode);
}
