package org.openldes.server.compaction.domain.repository;

import java.util.Collection;
import org.openldes.server.compaction.domain.entities.ViewCapacity;
import org.openldes.server.domain.model.ViewName;

public interface ViewCollection {
	void saveViewCapacity(ViewCapacity viewCapacity);

	void deleteViewCapacityByViewName(ViewName viewName);

	void deleteViewCapacitiesByCollectionName(String collectionName);

	Collection<ViewCapacity> getAllViewCapacities();
}
