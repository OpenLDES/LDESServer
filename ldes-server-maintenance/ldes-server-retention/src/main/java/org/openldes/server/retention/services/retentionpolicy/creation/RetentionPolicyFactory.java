package org.openldes.server.retention.services.retentionpolicy.creation;

import org.openldes.server.domain.model.ViewSpecification;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

public interface RetentionPolicyFactory {
    Optional<RetentionPolicy> extractRetentionPolicy(ViewSpecification viewSpecification);
    Optional<RetentionPolicy> extractRetentionPolicy(List<Model> retentionPolicies);
}
