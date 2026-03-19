package org.openldes.server.maintenance.repository;

import org.openldes.server.domain.model.ViewName;

import java.util.List;

public interface PageMemberRepository {
	void setPageMembersToNewPage(long newPageId, List<Long> pageIds);

	void deleteByViewNameAndMembersIds(ViewName viewName, List<Long> memberIds);
}
