package org.openmrs.module.reporting.report.task;

/**
 * If there are any non-persisted reports in the cache, persist them.  
 * Remove oldest reports from Cache if max has been reached
 */
public class PersistCachedReportsTask extends ReportingTask {

	@Override
	public synchronized void executeTask() {
		getReportService().persistCachedReports();
	}
}
