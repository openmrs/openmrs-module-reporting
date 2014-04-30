package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.PriorityComparator;
import org.openmrs.module.reporting.report.ReportRequest.Status;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * If there are any queued reports to run, this task starts running the next one.
 */
public class RunQueuedReportsTask extends ReportingTask {
	
	private final Log log = LogFactory.getLog(RunQueuedReportsTask.class);
	
	private static Integer maxExecutions = null;
	private final static Map<String, RunQueuedReportsTask> currentlyRunningRequests = new ConcurrentHashMap<String, RunQueuedReportsTask>();

	@Override
	public synchronized void executeTask() {
		if (maxExecutions == null) {
			maxExecutions = ReportingConstants.GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN();
		}

		log.debug("Executing the Run Queued Reports Task.");

		if (currentlyRunningRequests.size() < maxExecutions) {
			List<ReportRequest> l = getReportService().getReportRequests(null, null, null, Status.REQUESTED);
			if (!l.isEmpty()) {
				Collections.sort(l, new PriorityComparator());
				for (int i=1; i<l.size(); i++) {
					getReportService().logReportMessage(l.get(i), "Report in queue at position " + i);
				}
				ReportRequest requestToRun = l.get(0);
				ParameterizableUtil.refreshMappedDefinition(requestToRun.getReportDefinition());
				if (requestToRun.getBaseCohort() != null) {
					ParameterizableUtil.refreshMappedDefinition(requestToRun.getBaseCohort());
				}

				if (!currentlyRunningRequests.containsKey(requestToRun.getUuid())) {
					try {
						currentlyRunningRequests.put(requestToRun.getUuid(), this);
						getReportService().runReport(requestToRun);
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
