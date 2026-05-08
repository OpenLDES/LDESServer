package org.openldes.server.fragmentation.batch;

import static org.openldes.server.fragmentation.batch.BucketStepDefinitions.BUCKETISATION_STEP;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FragmentationJobDefinitions {
	public static final String FRAGMENTATION_JOB = "fragmentation";

	@Bean
	public SimpleJobBuilder fragmentationJobBuilder(JobRepository jobRepository,
	                                                @Qualifier(BUCKETISATION_STEP) Step bucketisationStep,
	                                                Step paginationStep,
	                                                JobExecutionListener paginationMetricUpdater,
	                                                JobExecutionListener fragmentationReadinessListener) {
		return new JobBuilder(FRAGMENTATION_JOB, jobRepository)
				.start(bucketisationStep)
				.next(paginationStep)
				.listener(paginationMetricUpdater)
				.listener(fragmentationReadinessListener);
	}
}
