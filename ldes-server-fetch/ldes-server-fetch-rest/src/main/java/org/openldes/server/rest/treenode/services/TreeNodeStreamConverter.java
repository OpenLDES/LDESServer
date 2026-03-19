package org.openldes.server.rest.treenode.services;

import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeStreamConverter {
    Model getMetaDataStatements(TreeNode treeNode);

    Model getMemberStatements(Member member, String treeNodeId);
}
