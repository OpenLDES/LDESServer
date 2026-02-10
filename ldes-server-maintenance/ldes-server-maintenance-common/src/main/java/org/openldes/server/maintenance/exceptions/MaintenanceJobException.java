package org.openldes.server.maintenance.exceptions;

import org.springframework.batch.core.JobExecutionException;

public class MaintenanceJobException extends RuntimeException {
	public MaintenanceJobException(JobExecutionException cause) {
		super(cause);
	}
}
