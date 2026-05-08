package org.openldes.server.rest.treenode.services;

import org.apache.jena.rdf.model.Model;
import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;

public interface TreeNodeStreamConverter {
    Model getMetaDataStatements(TreeNode treeNode);

    Model getMemberStatements(Member member, String treeNodeId);
}
