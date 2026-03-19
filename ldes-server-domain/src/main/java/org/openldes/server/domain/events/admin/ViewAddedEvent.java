package org.openldes.server.domain.events.admin;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;

/**
 * This event is published when a new view is created.
 * To communicate view config on startup, refer to {@link ViewInitializationEvent}.
 */
public class ViewAddedEvent implements ViewSupplier {
	private final ViewSpecification viewSpecification;

	public ViewAddedEvent(ViewSpecification viewSpecification) {
		this.viewSpecification = viewSpecification;
	}

	public ViewName getViewName() {
		return viewSpecification.getName();
	}

	public ViewSpecification viewSpecification() {
		return viewSpecification;
	}
}
