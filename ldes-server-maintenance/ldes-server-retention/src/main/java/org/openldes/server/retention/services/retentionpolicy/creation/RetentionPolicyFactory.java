package org.openldes.server.retention.services.retentionpolicy.creation;

import java.util.List;
import java.util.Optional;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.domain.model.ViewSpecification;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

public interface RetentionPolicyFactory {
    Optional<RetentionPolicy> extractRetentionPolicy(ViewSpecification viewSpecification);
    Optional<RetentionPolicy> extractRetentionPolicy(List<Model> retentionPolicies);
}
