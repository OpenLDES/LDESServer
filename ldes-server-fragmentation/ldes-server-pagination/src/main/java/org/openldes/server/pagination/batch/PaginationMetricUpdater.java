package org.openldes.server.pagination.batch;

import static org.openldes.server.fragmentation.batch.BucketStepDefinitions.BUCKETISATION_STEP;

import java.util.Objects;
import org.openldes.server.fragmentation.metrics.FragmentationMetricsService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaginationMetricUpdater implements JobExecutionListener {
	private final JdbcTemplate jdbcTemplate;
	private final FragmentationMetricsService fragmentationMetricsService;

	public PaginationMetricUpdater(JdbcTemplate jdbcTemplate, FragmentationMetricsService fragmentationMetricsService) {
		this.jdbcTemplate = jdbcTemplate;
		this.fragmentationMetricsService = fragmentationMetricsService;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		long viewId = Objects.requireNonNull(jobExecution.getJobParameters().getLong("viewId"));
		String collectionName = Objects.requireNonNull(jobExecution.getJobParameters().getString("collectionName"));

		updateViewStats(getBucketisedMemberCount(jobExecution), viewId);
		fragmentationMetricsService.updatePaginationCounts(collectionName);
	}


	private long getBucketisedMemberCount(JobExecution jobExecution) {
		return jobExecution.getStepExecutions().stream()
				.filter(stepExecution -> stepExecution.getStepName().equals(BUCKETISATION_STEP))
				.findFirst()
				.map(StepExecution::getWriteCount)
				.orElse(0L);
	}

	private void updateViewStats(long uniqueMemberCount, long viewId) {
		String sql = """
				UPDATE view_stats vs
				SET paginated_count = vs.paginated_count + ?
				where view_id = ?;
				""";
		jdbcTemplate.update(sql, uniqueMemberCount, viewId);
	}
}
