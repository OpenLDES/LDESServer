package org.openldes.server.retention.services.retentionpolicy.execution;

import java.util.List;
import org.openldes.server.retention.entities.MemberProperties;

public interface MemberRemover {

	void removeMembersFromEventSource(List<MemberProperties> memberProperties);

	void deleteMembers(List<MemberProperties> memberProperties);
}
