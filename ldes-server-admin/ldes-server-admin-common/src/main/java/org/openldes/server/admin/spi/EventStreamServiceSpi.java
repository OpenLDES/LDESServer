package org.openldes.server.admin.spi;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface EventStreamServiceSpi {
	List<EventStreamTO> retrieveAllEventStreams();

	EventStreamTO retrieveEventStream(String collectionName);

	Model getComposedDcat();
}
