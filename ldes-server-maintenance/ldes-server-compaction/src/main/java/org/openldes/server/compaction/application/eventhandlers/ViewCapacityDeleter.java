package org.openldes.server.compaction.application.eventhandlers;

import org.openldes.server.compaction.domain.repository.ViewCollection;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.events.admin.ViewDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewCapacityDeleter {
	private final ViewCollection viewCollection;

	public ViewCapacityDeleter(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		viewCollection.deleteViewCapacityByViewName(event.getViewName());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		viewCollection.deleteViewCapacitiesByCollectionName(event.collectionName());
	}

}
