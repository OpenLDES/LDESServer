package org.openldes.server.compaction.domain.repository;

import org.openldes.server.compaction.domain.entities.ViewCapacity;
import org.openldes.server.domain.model.ViewName;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ViewCollectionImpl implements ViewCollection {
    private final Map<ViewName, ViewCapacity> viewCapacities = new ConcurrentHashMap<>();

    @Override
    public void saveViewCapacity(ViewCapacity viewCapacity) {
        viewCapacities.put(viewCapacity.getViewName(), viewCapacity);
    }

    @Override
    public void deleteViewCapacityByViewName(ViewName viewName) {
        viewCapacities.remove(viewName);
    }

    @Override
    public void deleteViewCapacitiesByCollectionName(String collectionName) {
        viewCapacities.keySet().stream()
                .filter(viewName -> viewName.getCollectionName().equals(collectionName))
                .toList()
                .forEach(viewCapacities::remove);
    }

    @Override
    public Collection<ViewCapacity> getAllViewCapacities() {
        return viewCapacities.values();
    }
}
