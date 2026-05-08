package org.openldes.server.admin.spi;

import java.util.List;
import org.apache.jena.rdf.model.Model;

public interface EventStreamServiceSpi {
	List<EventStreamTO> retrieveAllEventStreams();

	EventStreamTO retrieveEventStream(String collectionName);

	Model getComposedDcat();
}
