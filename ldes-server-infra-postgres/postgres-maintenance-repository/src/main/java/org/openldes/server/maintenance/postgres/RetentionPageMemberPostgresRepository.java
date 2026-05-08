package org.openldes.server.maintenance.postgres;

import java.util.List;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.maintenance.postgres.repository.RetentionPageMemberEntityRepository;
import org.openldes.server.maintenance.repository.PageMemberRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RetentionPageMemberPostgresRepository implements PageMemberRepository {

	private final RetentionPageMemberEntityRepository entityRepository;

	public RetentionPageMemberPostgresRepository(RetentionPageMemberEntityRepository entityRepository) {
		this.entityRepository = entityRepository;
	}

	@Override
	@Transactional
	public void setPageMembersToNewPage(long newPageId, List<Long> pageIds) {
		entityRepository.setPageMembersToNewPage(newPageId, pageIds);
	}

	@Override
	@Modifying
	@Transactional
	public void deleteByViewNameAndMembersIds(ViewName viewName, List<Long> memberIds) {
		entityRepository.removePageMembers(viewName.getCollectionName(), viewName.getViewName(), memberIds);
	}
}
