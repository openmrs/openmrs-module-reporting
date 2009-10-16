package org.openmrs.module.report.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.scheduler.tasks.ProcessHL7InQueueTask;


public class DeleteOldReportsTask extends AbstractTask {
	
	// Logger
	private static Log log = LogFactory.getLog(ProcessHL7InQueueTask.class);
	
	@Override
	public void execute() {
		if (!isExecuting) {
			isExecuting = true;
			log.debug("Starting to execute DeleteOldReportsTask");
			Context.openSession();
			try {
				if (!Context.isAuthenticated()) {
					authenticate();
				}
				Context.getService(ReportService.class).deleteOldReportRequests();
			} catch (APIException ex) {
				log.debug("Task not executed because module is not loaded");
			} finally {
				log.debug("Finishing DeleteOldReportsTask");
				isExecuting = false;
				Context.closeSession();
			}
		}
	}
		
}
