package org.openldes.server.retention.repositories.retentionpolicies;

import org.openldes.server.maintenance.services.RetentionPolicyEmptinessChecker;

import java.util.Set;

public interface RetentionPolicyCollection<T> extends RetentionPolicyEmptinessChecker {
	Set<T> getRetentionPolicies();
}
