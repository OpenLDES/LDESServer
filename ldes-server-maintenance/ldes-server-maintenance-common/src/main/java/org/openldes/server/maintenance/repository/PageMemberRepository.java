package org.openldes.server.maintenance.repository;

import java.util.List;
import org.openldes.server.domain.model.ViewName;

public interface PageMemberRepository {
	void setPageMembersToNewPage(long newPageId, List<Long> pageIds);

	void deleteByViewNameAndMembersIds(ViewName viewName, List<Long> memberIds);
}
