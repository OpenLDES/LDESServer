package org.openldes.server.domain.model;

import java.util.List;
import java.util.Objects;
import org.apache.jena.rdf.model.Model;

public record EventSource(String collectionName, List<Model> retentionPolicies) {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EventSource eventSource = (EventSource) o;
        return collectionName.equals(eventSource.collectionName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionName);
    }
}
