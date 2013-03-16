package org.openmrs.module.reporting.report.task;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.PriorityComparator;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any queued reports to run, this task starts running the next one.
 */
public class RunQueuedReportsTask extends AbstractReportsTask {
	
	private final Log log = LogFactory.getLog(RunQueuedReportsTask.class);
	
	private static Integer maxExecutions = null;
	private final static Map<String, RunQueuedReportsTask> currentlyRunningRequests = new ConcurrentHashMap<String, RunQueuedReportsTask>();
	
	/**
	 * @see AbstractReportsTask#execute()
	 */
	@Override
	public synchronized void execute() {
		try {
			Thread.sleep(1000);  // This should not be necessary, but there is something funky with transaction order and this helps things to work
		}
		catch (Exception e) {}
		
		if (maxExecutions == null) {
			maxExecutions = ReportingConstants.GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN();
		}
		
		log.debug("Executing the Run Queued Reports Task");
		
		ReportService rs = Context.getService(ReportService.class);
		
		if (currentlyRunningRequests.size() < maxExecutions) {
			List<ReportRequest> l = rs.getReportRequests(null, null, null, Status.REQUESTED);
			if (!l.isEmpty()) {
				Collections.sort(l, new PriorityComparator());
				for (int i=1; i<l.size(); i++) {
					rs.logReportMessage(l.get(i), "Report in queue at position " + i);
				}
				ReportRequest requestToRun = l.get(0);
		    	ParameterizableUtil.refreshMappedDefinition(requestToRun.getReportDefinition());
		    	if (requestToRun.getBaseCohort() != null) {
		    		ParameterizableUtil.refreshMappedDefinition(requestToRun.getBaseCohort());
		    	}

		    	if (!currentlyRunningRequests.containsKey(requestToRun.getUuid())) {
					try {
						currentlyRunningRequests.put(requestToRun.getUuid(), this);
						rs.runReport(requestToRun);
					}
					finally {
						currentlyRunningRequests.remove(requestToRun.getUuid());
					}
				}
			}
		}
	}
	
	public static Map<String, RunQueuedReportsTask> getCurrentlyRunningRequests() {
		return currentlyRunningRequests;
	}
}
