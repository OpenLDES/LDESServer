package org.openldes.server.retention.services.retentionpolicy.execution;

import org.openldes.server.retention.entities.MemberProperties;

import java.util.List;

public interface MemberRemover {

	void removeMembersFromEventSource(List<MemberProperties> memberProperties);

	void deleteMembers(List<MemberProperties> memberProperties);
}
