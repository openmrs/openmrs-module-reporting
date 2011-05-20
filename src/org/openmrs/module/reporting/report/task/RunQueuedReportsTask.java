package org.openmrs.module.reporting.report.task;

import java.util.Collections;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.PriorityComparator;
import org.openmrs.module.reporting.report.ReportRequest.Status;
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
				
				ReportService rs = Context.getService(ReportService.class);
				List<ReportRequest> inProgress = rs.getReportRequests(null, null, null, Status.PROCESSING);
				int maxAtATime = ReportingConstants.GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN();
				if (inProgress.size() >= maxAtATime) {
					return;
				}
				List<ReportRequest> l = rs.getReportRequests(null, null, null, Status.REQUESTED);
				if (l.isEmpty()) {
					return;
				}
				
				Collections.sort(l, new PriorityComparator());
				ReportRequest requestToRun = l.get(0);
		    	ParameterizableUtil.refreshMappedDefinition(requestToRun.getReportDefinition());
		    	if (requestToRun.getBaseCohort() != null) {
		    		ParameterizableUtil.refreshMappedDefinition(requestToRun.getBaseCohort());
		    	}
		    	Report report = rs.runReport(requestToRun);
		    	rs.saveReport(report, null);
			} 
			finally {
				if (Context.isSessionOpen()) {
					Context.closeSession();
				}
				isExecuting = false;
			}
		}
	}
}
