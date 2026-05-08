package org.openldes.server.domain.events.admin;

import java.util.List;
import org.apache.jena.rdf.model.Model;

public record DeletionPolicyChangedEvent(String collectionName, List<Model> retentionPolicies) {
}
