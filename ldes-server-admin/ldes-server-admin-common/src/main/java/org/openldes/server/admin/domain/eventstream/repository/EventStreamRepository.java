package org.openldes.server.admin.domain.eventstream.repository;

import java.util.List;
import java.util.Optional;
import org.openldes.server.admin.spi.EventStreamTO;
import org.openldes.server.domain.model.EventStream;

public interface EventStreamRepository {
	List<EventStream> retrieveAllEventStreams();

	List<EventStreamTO> retrieveAllEventStreamTOs();

	Optional<EventStream> retrieveEventStream(String collectionName);

	Optional<EventStreamTO> retrieveEventStreamTO(String collectionName);

	Integer saveEventStream(EventStreamTO eventStreamTO);

	int deleteEventStream(String collectionName);

    void closeEventStream(String collectionName);
}
