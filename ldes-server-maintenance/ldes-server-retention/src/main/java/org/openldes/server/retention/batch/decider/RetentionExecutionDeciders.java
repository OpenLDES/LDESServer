package org.openldes.server.retention.batch.decider;

import org.openldes.server.maintenance.services.RetentionPolicyEmptinessChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RetentionExecutionDeciders {
	@Bean
	public RetentionExecutionDecider eventSourceRetentionExecutionDecider(List<RetentionPolicyEmptinessChecker> emptinessCheckers) {
		return new RetentionExecutionDecider(emptinessCheckers);
	}

	@Bean
	public RetentionExecutionDecider viewRetentionExecutionDecider(RetentionPolicyEmptinessChecker viewRetentionPolicyCollection) {
		return new RetentionExecutionDecider(List.of(viewRetentionPolicyCollection));
	}
}
