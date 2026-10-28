package org.openldes.server.fetch.postgres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.openldes.server.domain.events.admin.EventStreamCreatedEvent;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.model.EventStream;
import org.openldes.server.fetch.postgres.mapper.TreeNodeMapper;
import org.openldes.server.fetch.postgres.projection.TreeNodeProjection;
import org.openldes.server.fetch.postgres.repository.FetchPageEntityRepository;
import org.openldes.server.fetch.postgres.repository.FetchPageMemberEntityRepository;
import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.repository.TreeNodeRepository;
import org.openldes.server.fetching.services.versioncreation.VersionObjectCreator;
import org.openldes.server.fetching.services.versioncreation.VersionObjectCreatorFactory;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

@Repository
public class TreeNodePostgresRepository implements TreeNodeRepository {
	private final FetchPageEntityRepository pageEntityRepository;
	private final FetchPageMemberEntityRepository pageMemberEntityRepository;
	private final Map<String, VersionObjectCreator> versionObjectCreatorMap = new HashMap<>();

	public TreeNodePostgresRepository(FetchPageEntityRepository pageEntityRepository, FetchPageMemberEntityRepository pageMemberEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
		this.pageMemberEntityRepository = pageMemberEntityRepository;
	}

	@Override
	public Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier) {
		return findProjection(fragmentIdentifier)
				.map(page -> {
					var versionObjectCreator = versionObjectCreatorMap.get(page.getBucket().getView().getEventStream().getName());

					final List<Member> members = pageMemberEntityRepository.findAllMembersByPageId(page.getId())
							.stream()
							.map(treeMemberProjection -> new Member(treeMemberProjection.getSubject(),
									versionObjectCreator.createFromMember(treeMemberProjection.getSubject(),
											treeMemberProjection.getModel(), treeMemberProjection.getVersionOf(),
											treeMemberProjection.getTimestamp())))
							.toList();
					return TreeNodeMapper.fromProjection(page, members);
				});
	}

	@Override
	public Optional<TreeNode> findTreeNodeWithoutMembers(LdesFragmentIdentifier fragmentIdentifier) {
		return findProjection(fragmentIdentifier)
				.map(projection -> TreeNodeMapper.fromProjection(projection, List.of()));
	}

	/**
	 * Keep the unique-index lookup for the usual exact request. On a miss, resolve
	 * the same query parameters in any order through a second, indexed lookup.
	 * Return the stored URL so linked-data output retains its original identity.
	 */
	Optional<TreeNodeProjection> findProjection(LdesFragmentIdentifier identifier) {
		final String partialUrl = identifier.asDecodedFragmentId();
		final Optional<TreeNodeProjection> exact =
				pageEntityRepository.findTreeNodeProjectionByPartialUrl(partialUrl);
		if (exact.isPresent() || !partialUrl.contains("?")) {
			return exact;
		}

		final List<String> matches =
				pageEntityRepository.findPartialUrlsWithEquivalentParameters(partialUrl);
		if (matches.size() > 1) {
			throw new IllegalStateException("Multiple pages have equivalent fragment parameters: " + partialUrl);
		}
		return matches.stream().findFirst()
				.flatMap(pageEntityRepository::findTreeNodeProjectionByPartialUrl);
	}

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		final EventStream eventStream = event.eventStream();
		final VersionObjectCreator versionObjectCreator = VersionObjectCreatorFactory.createVersionObjectCreator(eventStream);
		versionObjectCreatorMap.put(eventStream.getCollection(), versionObjectCreator);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		versionObjectCreatorMap.remove(event.collectionName());
	}
}
