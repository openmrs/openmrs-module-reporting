package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any unsaved reports that are older than the expiration period set, delete them
 */
public class DeleteOldReportsTask extends AbstractReportsTask {
	
	private static Log log = LogFactory.getLog(DeleteOldReportsTask.class);
	
	/**
	 * @see AbstractReportsTask#execute()
	 */
	@Override
	public synchronized void execute() {		
		log.info("Executing the Delete Old Reports Task");
		Context.getService(ReportService.class).deleteOldReportRequests();
	}
}
