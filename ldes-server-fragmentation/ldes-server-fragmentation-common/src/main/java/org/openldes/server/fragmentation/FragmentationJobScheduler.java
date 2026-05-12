package org.openldes.server.fragmentation;

import static org.openldes.server.domain.constants.ServerConfig.FRAGMENTATION_CRON;
import static org.openldes.server.fragmentation.batch.BatchConfiguration.ASYNC_JOB_LAUNCHER;
import static org.openldes.server.fragmentation.batch.FragmentationJobDefinitions.FRAGMENTATION_JOB;

import java.time.LocalDateTime;
import java.util.Objects;
import org.openldes.server.fragmentation.entities.UnprocessedView;
import org.openldes.server.fragmentation.exceptions.FragmentationJobException;
import org.openldes.server.fragmentation.repository.UnprocessedViewRepository;
import org.openldes.server.fragmentation.valueobjects.ContinueFragmentationTriggerEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class FragmentationJobScheduler {
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW_NAME = "viewName";
	public static final String COLLECTION_ID = "collectionId";
	public static final String VIEW_ID = "viewId";
	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job fragmentationJob;
	private final UnprocessedViewRepository unprocessedViewRepository;

	public FragmentationJobScheduler(@Qualifier(ASYNC_JOB_LAUNCHER) JobLauncher jobLauncher,
	                                 JobExplorer jobExplorer,
	                                 SimpleJobBuilder fragmentationJobBuilder,
	                                 UnprocessedViewRepository unprocessedViewRepository) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.unprocessedViewRepository = unprocessedViewRepository;
		this.fragmentationJob = fragmentationJobBuilder.build();
	}

	@Scheduled(cron = FRAGMENTATION_CRON)
	public void scheduleJobs() {
		unprocessedViewRepository.findAll()
				.parallelStream()
				.filter(this::noJobsRunning)
				.map(unprocessedView -> new JobParametersBuilder()
						.addLong(VIEW_ID, (long) unprocessedView.viewId())
						.addLong(COLLECTION_ID, (long) unprocessedView.collectionId())
						.addString(VIEW_NAME, unprocessedView.viewName())
						.addString(COLLECTION_NAME, unprocessedView.collectionName())
						.addLocalDateTime("triggered", LocalDateTime.now())
						.toJobParameters())
				.forEach(this::launchSingleFragmentationJob);
	}

	@EventListener
	public void handleContinueFragmentationTriggerEvent(ContinueFragmentationTriggerEvent event) {
		launchSingleFragmentationJob(event.getNewlyTriggeredJobParameters());
	}

	private boolean noJobsRunning(UnprocessedView unprocessedView) {
		return jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)
				.stream()
				.map(JobExecution::getJobParameters)
				.map(params -> new UnprocessedView(
						Objects.requireNonNull(params.getLong(COLLECTION_ID)).intValue(),
						params.getString(COLLECTION_NAME),
						Objects.requireNonNull(params.getLong(VIEW_ID)).intValue(),
						params.getString(VIEW_NAME)
				))
				.noneMatch(unprocessedViewFromParams -> Objects.equals(unprocessedView, unprocessedViewFromParams));
	}

	private void launchSingleFragmentationJob(JobParameters jobParams) {
		try {
			jobLauncher.run(fragmentationJob, jobParams);
		} catch (JobExecutionException e) {
			throw new FragmentationJobException(e);
		}
	}
}
