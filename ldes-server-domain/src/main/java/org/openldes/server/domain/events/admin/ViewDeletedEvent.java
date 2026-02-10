package org.openldes.server.domain.events.admin;

import org.openldes.server.domain.model.ViewName;

public class ViewDeletedEvent {
	private final ViewName viewName;

	public ViewDeletedEvent(ViewName viewName) {
		this.viewName = viewName;
	}

	public ViewName getViewName() {
		return viewName;
	}
}
