package org.openldes.server.fragmentation.valueobjects;

import java.time.LocalDateTime;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

public class ContinueFragmentationTriggerEvent {
	final JobParameters originalJobParameters;

	public ContinueFragmentationTriggerEvent(JobParameters originalJobParameters) {
		this.originalJobParameters = originalJobParameters;
	}

	public JobParameters getNewlyTriggeredJobParameters() {
		return new JobParametersBuilder(originalJobParameters).addLocalDateTime("triggered", LocalDateTime.now()).toJobParameters();
	}
}
