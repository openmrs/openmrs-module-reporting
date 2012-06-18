package org.openmrs.module.reporting.report.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any non-persisted reports in the cache, persist them.  
 * Remove oldest reports from Cache if max has been reached
 */
public class PersistCachedReportsTask extends AbstractReportsTask {
	
	/**
	 * @see AbstractReportsTask#execute()
	 */
	@Override
	public synchronized void execute() {
		Context.getService(ReportService.class).persistCachedReports();
	}
}
