package org.openldes.server.fetch.postgres;

import org.openldes.server.domain.events.admin.EventStreamCreatedEvent;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.model.EventStream;
import org.openldes.server.fetch.postgres.mapper.TreeNodeMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		return pageEntityRepository
				.findTreeNodeProjectionByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
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
		return pageEntityRepository
				.findTreeNodeProjectionByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
				.map(projection -> TreeNodeMapper.fromProjection(projection, List.of()));
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
