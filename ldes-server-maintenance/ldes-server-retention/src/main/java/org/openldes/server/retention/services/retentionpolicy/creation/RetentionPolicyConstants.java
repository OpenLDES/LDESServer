package org.openldes.server.retention.services.retentionpolicy.creation;

import static org.openldes.server.domain.constants.RdfConstants.LDES;

public class RetentionPolicyConstants {

	private RetentionPolicyConstants() {
		// Class of constants
	}

	public static final String TIME_BASED_RETENTION_POLICY = LDES + "DurationAgoPolicy";
	public static final String VERSION_BASED_RETENTION_POLICY = LDES + "LatestVersionSubset";
}
