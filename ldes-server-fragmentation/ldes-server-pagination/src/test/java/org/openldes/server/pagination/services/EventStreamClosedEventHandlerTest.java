package org.openldes.server.pagination.services;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openldes.server.domain.events.admin.EventStreamClosedEvent;
import org.openldes.server.pagination.repositories.PageRepository;

@ExtendWith(MockitoExtension.class)
class EventStreamClosedEventHandlerTest {
	@Mock
	private PageRepository pageRepository;
	@InjectMocks
	private EventStreamClosedEventHandler eventStreamClosedEventHandler;

	@Test
	void when_EventStreamClosedEvent_then_FragmentsAreMadeImmutable() {
		final String collectionName = "collectionName";
		EventStreamClosedEvent event = new EventStreamClosedEvent(collectionName);

		eventStreamClosedEventHandler.onEventStreamClosed(event);

		verify(pageRepository).markAllPagesImmutableByCollectionName(collectionName);
	}
}
