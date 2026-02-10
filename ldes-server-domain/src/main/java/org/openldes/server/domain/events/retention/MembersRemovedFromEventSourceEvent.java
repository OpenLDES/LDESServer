package org.openldes.server.domain.events.retention;

import java.util.List;

public record MembersRemovedFromEventSourceEvent(List<String> ids) {
}
