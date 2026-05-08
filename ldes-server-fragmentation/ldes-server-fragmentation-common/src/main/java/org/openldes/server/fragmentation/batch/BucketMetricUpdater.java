package org.openldes.server.fragmentation.batch;

import static org.openldes.server.fragmentation.FragmentationJobScheduler.COLLECTION_NAME;

import org.jetbrains.annotations.NotNull;
import org.openldes.server.fragmentation.metrics.FragmentationMetricsService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class BucketMetricUpdater implements StepExecutionListener {
	private final FragmentationMetricsService fragmentationMetricsService;

	public BucketMetricUpdater(FragmentationMetricsService fragmentationMetricsService) {
		this.fragmentationMetricsService = fragmentationMetricsService;
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		fragmentationMetricsService.updateBucketCounts(stepExecution.getJobParameters().getString(COLLECTION_NAME));
		return StepExecutionListener.super.afterStep(stepExecution);
	}
}
