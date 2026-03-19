package org.openldes.server.retention.entities;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.Objects;

public record ViewRetentionPolicyProvider(ViewName viewName, RetentionPolicy retentionPolicy) implements RetentionPolicyProvider {

	@Override
	public String getName() {
		return viewName().asString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ViewRetentionPolicyProvider that)) return false;
		return Objects.equals(viewName, that.viewName);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + Objects.hashCode(viewName);
		return result;
	}
}
