package org.openldes.server.fetching.services;

import java.util.stream.Stream;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.repository.TreeMemberRepository;
import org.openldes.server.fetching.repository.TreeNodeRepository;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.springframework.stereotype.Component;

@Component
public class StreamingTreeNodeFactoryImpl implements StreamingTreeNodeFactory {

    private final TreeNodeRepository treeNodeRepository;
    private final TreeMemberRepository treeMemberRepository;

    public StreamingTreeNodeFactoryImpl(TreeNodeRepository treeNodeRepository, TreeMemberRepository treeMemberRepository) {
	    this.treeNodeRepository = treeNodeRepository;
	    this.treeMemberRepository = treeMemberRepository;
    }

    @Override
    public TreeNode getFragmentWithoutMemberData(LdesFragmentIdentifier treeNodeId) {
        return treeNodeRepository.findTreeNodeWithoutMembers(treeNodeId)
                .orElseThrow(() -> new MissingResourceException("fragment", treeNodeId.asDecodedFragmentId()));
    }

    @Override
    public Stream<Member> getMembersOfFragment(LdesFragmentIdentifier treeNodeId) {
        return treeMemberRepository.findAllByTreeNodeUrl(treeNodeId.asDecodedFragmentId());
    }
}
