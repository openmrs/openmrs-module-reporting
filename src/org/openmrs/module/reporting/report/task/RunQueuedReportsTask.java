package org.openmrs.module.reporting.report.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * If there are any queued reports to run, this task starts running the next one.
 */
public class RunQueuedReportsTask extends AbstractTask {
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		if (!isExecuting()) {
			isExecuting = true;
			try {
				Context.openSession();
				if (!Context.isAuthenticated()) {
					authenticate();
				}
				Context.getService(ReportService.class).maybeRunNextQueuedReport();
			} finally {
				if (Context.isSessionOpen())
					Context.closeSession();
				isExecuting = false;
			}
		}
	}
	
}
