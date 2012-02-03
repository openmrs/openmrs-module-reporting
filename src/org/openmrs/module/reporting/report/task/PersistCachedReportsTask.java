package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any non-persisted reports in the cache, persist them.  
 * Remove oldest reports from Cache if max has been reached
 */
public class PersistCachedReportsTask extends AbstractReportsTask {
	
	private static Log log = LogFactory.getLog(DeleteOldReportsTask.class);
	
	/**
	 * @see AbstractReportsTask#execute()
	 */
	@Override
	public synchronized void execute() {		
		log.info("Executing the Persist Cached Reports Task");
		Context.getService(ReportService.class).persistCachedReports();
	}
}
