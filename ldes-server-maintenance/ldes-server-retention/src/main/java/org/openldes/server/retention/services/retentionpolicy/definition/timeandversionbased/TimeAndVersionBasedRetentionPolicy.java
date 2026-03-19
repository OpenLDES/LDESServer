package org.openldes.server.retention.services.retentionpolicy.definition.timeandversionbased;

import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;
import org.openldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import org.openldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

import java.time.Duration;

import static org.openldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.TIME_BASED_RETENTION_POLICY;
import static org.openldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.VERSION_BASED_RETENTION_POLICY;

public record TimeAndVersionBasedRetentionPolicy(Duration duration,
                                                 int numberOfMembersToKeep) implements RetentionPolicy {

    public static TimeAndVersionBasedRetentionPolicy from(RetentionPolicy policyA, RetentionPolicy policyB) {
        if (policyA instanceof TimeBasedRetentionPolicy(Duration duration)) {
            verifyIsTypeVersionBased(policyB);
            final int numberOfMembersToKeep = ((VersionBasedRetentionPolicy) policyB).numberOfMembersToKeep();
            return new TimeAndVersionBasedRetentionPolicy(duration, numberOfMembersToKeep);
        } else if (policyB instanceof TimeBasedRetentionPolicy(Duration duration)) {
            verifyIsTypeVersionBased(policyA);
            final int numberOfMembersToKeep = ((VersionBasedRetentionPolicy) policyA).numberOfMembersToKeep();
            return new TimeAndVersionBasedRetentionPolicy(duration, numberOfMembersToKeep);
        } else {
            throw timebasedAndVersionBasedRequiredException();
        }
    }

    private static void verifyIsTypeVersionBased(RetentionPolicy maybeVersionBasedPolicy) {
        if (!(maybeVersionBasedPolicy instanceof VersionBasedRetentionPolicy)) {
            throw timebasedAndVersionBasedRequiredException();
        }
    }

    private static IllegalArgumentException timebasedAndVersionBasedRequiredException() {
        return new IllegalArgumentException(
                "TimeAndVersionBasedRetentionPolicy requires exactly one %s and one %s"
                        .formatted(TIME_BASED_RETENTION_POLICY, VERSION_BASED_RETENTION_POLICY)
        );
    }

    @Override
    public RetentionPolicyType getType() {
        return RetentionPolicyType.TIME_AND_VERSION_BASED;
    }

}
