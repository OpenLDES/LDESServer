package org.openldes.server.retention.entities;

import java.util.Objects;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

public record EventSourceRetentionPolicyProvider(String collectionName,
                                                 RetentionPolicy retentionPolicy) implements RetentionPolicyProvider {

	@Override
	public String getName() {
		return collectionName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventSourceRetentionPolicyProvider that)) return false;
		return Objects.equals(collectionName, that.collectionName);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + Objects.hashCode(collectionName);
		return result;
	}
}
