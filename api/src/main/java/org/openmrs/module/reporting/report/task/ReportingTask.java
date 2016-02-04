package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any unsaved reports that are older than the expiration period set, delete them
 */
public abstract class ReportingTask implements Runnable {

	private transient final Log log = LogFactory.getLog(getClass());

	// Properties that should be set on this task when it is instantiated, before it is run
	private long scheduledExecutionTime;
	private Thread thread;

	@Override
	public void run() {
		try {
			log.info("Running reporting task: " + getClass().getSimpleName());
			thread = Thread.currentThread();
			executeTask();
			thread = null;
			log.info("Completed reporting task: " + getClass().getSimpleName());
		}
		catch (Exception e) {
			log.error("An error occurred while running reporting task: " + getClass(), e);
		}
	}

	/**
	 * Subclasses should implement this method with the actual logic for this task
	 */
	protected abstract void executeTask();

	/**
	 * Attempt to cancel the currently running task by closing the hibernate session
	 */
	public void cancelTask() {
		log.info("Attempting to cancel " + getClass().getSimpleName());
		if (thread != null) {
			thread.stop();
			log.info(getClass().getSimpleName() + " task has been cancelled");
		}
		else {
			log.info("Did not have to cancel " + getClass().getSimpleName() + " task as it was not running");
		}
	}

	public long getScheduledExecutionTime() {
		return scheduledExecutionTime;
	}

	public void setScheduledExecutionTime(long scheduledExecutionTime) {
		this.scheduledExecutionTime = scheduledExecutionTime;
	}

	protected ReportService getReportService() {
		return Context.getService(ReportService.class);
	}
}
