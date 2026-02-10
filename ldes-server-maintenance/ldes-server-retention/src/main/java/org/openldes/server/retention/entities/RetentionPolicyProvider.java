package org.openldes.server.retention.entities;

import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

public interface RetentionPolicyProvider {
	String getName();
	RetentionPolicy retentionPolicy();

}
