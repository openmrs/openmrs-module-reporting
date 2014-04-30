package org.openmrs.module.reporting.report.task;

/**
 * If there are any unsaved reports that are older than the expiration period set, delete them
 */
public class DeleteOldReportsTask extends ReportingTask {

	@Override
	public synchronized void executeTask() {
		getReportService().deleteOldReportRequests();
	}
}
