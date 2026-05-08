package org.openldes.server.rest.treenode.services;

import java.util.List;
import org.apache.jena.rdf.model.Statement;
import org.openldes.server.fetching.entities.TreeNode;

public interface TreeNodeStatementCreator {
    List<Statement> addEventStreamStatements(TreeNode treeNode, String baseUrl);

    List<Statement> addTreeNodeStatements(TreeNode treeNode, String collectionName, String prefix);
}
