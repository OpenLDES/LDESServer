package org.openldes.server.domain.events.admin;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;

/**
 * This event is published on application startup to load the existing views.
 * For new views being added to the server, refer to {@link ViewAddedEvent}
 */
public record ViewInitializationEvent(ViewSpecification viewSpecification) implements ViewSupplier {
	public ViewName getViewName() {
		return viewSpecification.getName();
	}

}
