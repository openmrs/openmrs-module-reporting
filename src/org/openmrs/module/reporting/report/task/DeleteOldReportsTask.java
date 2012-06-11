package org.openmrs.module.reporting.report.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any unsaved reports that are older than the expiration period set, delete them
 */
public class DeleteOldReportsTask extends AbstractReportsTask {
	
	/**
	 * @see AbstractReportsTask#execute()
	 */
	@Override
	public synchronized void execute() {		
		Context.getService(ReportService.class).deleteOldReportRequests();
	}
}
