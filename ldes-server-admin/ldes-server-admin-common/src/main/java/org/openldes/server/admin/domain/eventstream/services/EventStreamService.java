package org.openldes.server.admin.domain.eventstream.services;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.spi.EventStreamServiceSpi;
import org.openldes.server.admin.spi.EventStreamTO;

public interface EventStreamService extends EventStreamServiceSpi {

	void deleteEventStream(String collectionName);

	EventStreamTO createEventStream(EventStreamTO eventStream);

	void updateEventSource(String collectionName, List<Model> eventSourceModel);

	void closeEventStream(String collectionName);
}
