package org.openldes.server.domain.events.retention;

import org.openldes.server.domain.model.ViewName;

public record MemberUnallocatedEvent(String memberId, ViewName viewName) {
}
