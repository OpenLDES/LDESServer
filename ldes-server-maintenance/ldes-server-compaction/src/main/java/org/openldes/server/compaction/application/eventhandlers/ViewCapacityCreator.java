package org.openldes.server.compaction.application.eventhandlers;

import org.openldes.server.compaction.domain.entities.ViewCapacity;
import org.openldes.server.compaction.domain.repository.ViewCollection;
import org.openldes.server.domain.events.admin.ViewAddedEvent;
import org.openldes.server.domain.events.admin.ViewInitializationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewCapacityCreator {
	private final ViewCollection viewCollection;

	public ViewCapacityCreator(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		viewCollection
				.saveViewCapacity(new ViewCapacity(event.getViewName(), event.viewSpecification().getPageSize()));
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		viewCollection
				.saveViewCapacity(new ViewCapacity(event.getViewName(), event.viewSpecification().getPageSize()));
	}

}
