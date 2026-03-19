package org.openldes.server.domain.events.admin;

import org.openldes.server.domain.model.ViewName;

public record DcatViewDeletedEvent(ViewName viewName) {
}
