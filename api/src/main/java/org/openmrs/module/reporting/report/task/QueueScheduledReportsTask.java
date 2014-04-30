package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.quartz.CronExpression;

import java.util.Calendar;
import java.util.Date;

/**
 * This task checks whether any reports are scheduled to be run at that time.
 * If it finds any matches, it clones them and adds the report request to the queue
 */
public class QueueScheduledReportsTask extends ReportingTask {
	
	private static Log log = LogFactory.getLog(QueueScheduledReportsTask.class);

	@Override
	public synchronized void executeTask() {

		// Retrieve the time at which this task was scheduled to execute, ignoring seconds
		Calendar currentCal = Calendar.getInstance();
		currentCal.setTimeInMillis(getScheduledExecutionTime());
		currentCal.set(Calendar.SECOND, 0);
		Date currentTime = currentCal.getTime();

		log.debug("Executing the Queue Scheduled Reports Task");

		// First, identify if there are any scheduled report requests that should be run at this moment
		// If there are, clone the request and move it to the REQUESTED status.  If this is the last
		// time this scheduled report can run, move it into the COMPLETED status.
		for (ReportRequest scheduledReport : getReportService().getReportRequests(null, null, null, Status.SCHEDULED)) {
			try {
				String cronSchedule = scheduledReport.getSchedule();
				CronExpression cron = new CronExpression(cronSchedule);
				if (cron.isSatisfiedBy(currentTime)) {
					log.info("Running scheduled report at " + currentTime + " which matches the schedule: " + scheduledReport.getSchedule());
					ReportRequest newRequest = new ReportRequest();
					newRequest.setStatus(Status.REQUESTED);
					newRequest.setReportDefinition(scheduledReport.getReportDefinition());
					newRequest.setBaseCohort(scheduledReport.getBaseCohort());
					newRequest.setRenderingMode(scheduledReport.getRenderingMode());
					newRequest.setPriority(scheduledReport.getPriority());
					newRequest.setDescription(scheduledReport.getDescription());
					newRequest.setProcessAutomatically(true);
					getReportService().saveReportRequest(newRequest);
				}

				Date nextInvalidTime = cron.getNextInvalidTimeAfter(currentTime);
				Date nextValidTime = cron.getNextValidTimeAfter(nextInvalidTime);

				if (nextValidTime == null) {
					scheduledReport.setStatus(Status.SCHEDULE_COMPLETED);
					getReportService().saveReportRequest(scheduledReport);
				}
			}
			catch (Throwable t) {
				log.error("Failed to request scheduled report", t);
				scheduledReport.setStatus(Status.FAILED);
				getReportService().saveReportRequest(scheduledReport);
			}
		}
	}
}
