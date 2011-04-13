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
		try {
			// TODO make this work pre-1.7, i.e. pre-daemon
			Context.openSession();
			Context.getService(ReportService.class).maybeRunNextQueuedReport();
		} finally {
			if (Context.isSessionOpen())
				Context.closeSession();
		}
	}
	
}
