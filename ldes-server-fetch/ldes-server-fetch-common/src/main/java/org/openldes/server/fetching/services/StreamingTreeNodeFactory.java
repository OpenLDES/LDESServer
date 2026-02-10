package org.openldes.server.fetching.services;

import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;

import java.util.stream.Stream;

public interface StreamingTreeNodeFactory {
    TreeNode getFragmentWithoutMemberData(LdesFragmentIdentifier treeNodeId);

    Stream<Member> getMembersOfFragment(LdesFragmentIdentifier treeNodeId);
}
