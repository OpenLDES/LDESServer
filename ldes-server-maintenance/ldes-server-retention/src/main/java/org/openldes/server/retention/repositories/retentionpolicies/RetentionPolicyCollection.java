package org.openldes.server.retention.repositories.retentionpolicies;

import java.util.Set;
import org.openldes.server.maintenance.services.RetentionPolicyEmptinessChecker;

public interface RetentionPolicyCollection<T> extends RetentionPolicyEmptinessChecker {
	Set<T> getRetentionPolicies();
}
