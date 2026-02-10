package org.openldes.server.retention.batch.decider;

import org.openldes.server.maintenance.services.RetentionPolicyEmptinessChecker;
import org.openldes.server.maintenance.valueobjects.DecidedFlowExecutionStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.List;

public class RetentionExecutionDecider implements JobExecutionDecider {
	private final List<RetentionPolicyEmptinessChecker> retentionEmptinessCheckers;

	public RetentionExecutionDecider(List<RetentionPolicyEmptinessChecker> retentionEmptinessCheckers) {
		this.retentionEmptinessCheckers = retentionEmptinessCheckers;
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		return retentionEmptinessCheckers.stream().allMatch(RetentionPolicyEmptinessChecker::isEmpty)
				? DecidedFlowExecutionStatus.SKIP.status()
				: DecidedFlowExecutionStatus.CONTINUE.status();
	}
}
