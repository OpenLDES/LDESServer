package org.openldes.server.fragmentation.batch;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openldes.server.fragmentation.batch.FragmentationJobDefinitions.FRAGMENTATION_JOB;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class OldJobsAbandonerTest {
	@Mock
	private JobExplorer jobExplorer;
	@Mock
	private JobRepository jobRepository;
	@InjectMocks
	private OldJobsAbandoner oldJobsAbandoner;

	@Test
	void test_Run() {
		when(jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)).thenReturn(Set.of(new JobExecution(1L)));

		oldJobsAbandoner.run();

		verify(jobRepository).update(new JobExecution(1L));
	}

	@Test
	void given_NoOldJobs_Then_UpdateNothing() {
		when(jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)).thenReturn(Set.of());

		oldJobsAbandoner.run();

		verify(jobExplorer).findRunningJobExecutions(FRAGMENTATION_JOB);
		verifyNoInteractions(jobRepository);
	}
}
