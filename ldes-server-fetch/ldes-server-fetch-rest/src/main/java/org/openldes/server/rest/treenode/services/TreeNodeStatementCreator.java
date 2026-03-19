package org.openldes.server.rest.treenode.services;

import org.openldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface TreeNodeStatementCreator {
    List<Statement> addEventStreamStatements(TreeNode treeNode, String baseUrl);

    List<Statement> addTreeNodeStatements(TreeNode treeNode, String collectionName, String prefix);
}
