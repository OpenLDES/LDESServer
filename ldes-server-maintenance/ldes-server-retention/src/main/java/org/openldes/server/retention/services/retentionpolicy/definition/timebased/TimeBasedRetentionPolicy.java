package org.openldes.server.retention.services.retentionpolicy.definition.timebased;

import java.time.Duration;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;

public record TimeBasedRetentionPolicy(Duration duration) implements RetentionPolicy {

	@Override
	public RetentionPolicyType getType() {
		return RetentionPolicyType.TIME_BASED;
	}

}
