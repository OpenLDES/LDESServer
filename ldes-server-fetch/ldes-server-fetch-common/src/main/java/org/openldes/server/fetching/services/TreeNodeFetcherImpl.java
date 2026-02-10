package org.openldes.server.fetching.services;

import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.repository.TreeNodeRepository;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.openldes.server.fetching.valueobjects.LdesFragmentRequest;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private final TreeNodeRepository treeNodeRepository;

	public TreeNodeFetcherImpl(TreeNodeRepository treeNodeRepository) {
		this.treeNodeRepository = treeNodeRepository;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		final ViewName viewName = ldesFragmentRequest.viewName();
		final LdesFragmentIdentifier ldesFragmentIdentifier = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(), ldesFragmentRequest.fragmentPairs());
		return treeNodeRepository
				.findByFragmentIdentifier(new LdesFragmentIdentifier(viewName, ldesFragmentRequest.fragmentPairs()))
				.orElseThrow(() -> new MissingResourceException("TreeNode", ldesFragmentIdentifier.asDecodedFragmentId()));
	}
}
