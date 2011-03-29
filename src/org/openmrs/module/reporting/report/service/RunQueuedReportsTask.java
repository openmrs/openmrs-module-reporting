package org.openmrs.module.reporting.report.service;

import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;


public class RunQueuedReportsTask extends AbstractTask {
	
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
