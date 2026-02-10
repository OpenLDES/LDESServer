package org.openldes.server.domain.events.retention;

import java.util.List;

public record MembersDeletedEvent(List<String> memberIds) {
}
