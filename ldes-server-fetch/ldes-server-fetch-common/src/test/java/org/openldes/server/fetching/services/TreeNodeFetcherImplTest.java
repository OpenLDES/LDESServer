package org.openldes.server.fetching.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.repository.TreeNodeRepository;
import org.openldes.server.fetching.valueobjects.FragmentPair;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.openldes.server.fetching.valueobjects.LdesFragmentRequest;

class TreeNodeFetcherImplTest {
	private static final String COLLECTION = "collectionName";
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private TreeNodeRepository treeNodeRepository;
	private TreeNodeFetcherImpl treeNodeFetcher;

	@BeforeEach
	void setUp() {
		treeNodeRepository = mock(TreeNodeRepository.class);
		treeNodeFetcher = new TreeNodeFetcherImpl(treeNodeRepository);
	}

	@Test
	void when_getFragment_WhenNoFragmentExists_ThenMissingResourceExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		LdesFragmentIdentifier ldesFragmentIdentifier = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs());
		when(treeNodeRepository.findByFragmentIdentifier(ldesFragmentIdentifier))
				.thenThrow(new MissingResourceException("TreeNode", ldesFragmentIdentifier.asDecodedFragmentId()));

		assertThatThrownBy(() -> treeNodeFetcher.getFragment(ldesFragmentRequest))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: TreeNode with id: /collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z could not be found.");
	}

	@Test
	void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		LdesFragmentIdentifier ldesFragmentIdentifier = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs());
		TreeNode treeNode = new TreeNode(ldesFragmentIdentifier.asDecodedFragmentId(), true, false, List.of(),
				List.of(), "collectionName", null);

		when(treeNodeRepository.findByFragmentIdentifier(ldesFragmentIdentifier)).thenReturn(Optional.of(treeNode));

		TreeNode returnedTreeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);

		assertThat(returnedTreeNode).isEqualTo(treeNode);
	}
}
