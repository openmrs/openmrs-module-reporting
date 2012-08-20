package org.openmrs.module.reporting.report.task;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.service.ReportService;
import org.quartz.CronExpression;

/**
 * This task should be scheduled to run exactly once per minute, to check whether any reports are scheduled to be run at that time.
 * If it finds any matches, it clones them and adds the report request to the queue
 */
public class QueueScheduledReportsTask extends AbstractReportsTask {
	
	private static Log log = LogFactory.getLog(QueueScheduledReportsTask.class);

	/**
	 * @see AbstractReportsTask#execute()
	 */
	@Override
	public void execute() {

		// Retrieve the time at which this task was scheduled to execute, ignoring seconds
		Calendar currentCal = Calendar.getInstance();
		currentCal.setTimeInMillis(scheduledExecutionTime());
		currentCal.set(Calendar.SECOND, 0);
		Date currentTime = currentCal.getTime();
		
		ReportService rs = Context.getService(ReportService.class);
		
		log.debug("Executing the Queue Scheduled Reports Task");
		
		// First, identify if there are any scheduled report requests that should be run at this moment
		// If there are, clone the request and move it to the REQUESTED status.  If this is the last
		// time this scheduled report can run, move it into the COMPLETED status.
		for (ReportRequest scheduledReport : rs.getReportRequests(null, null, null, Status.SCHEDULED)) {
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
					newRequest.setSaveAutomatically(true);
					rs.saveReportRequest(newRequest);
				}
				
				Date nextInvalidTime = cron.getNextInvalidTimeAfter(currentTime);
				Date nextValidTime = cron.getNextValidTimeAfter(nextInvalidTime);

				if (nextValidTime == null) {
					scheduledReport.setStatus(Status.SCHEDULE_COMPLETED);
					rs.saveReportRequest(scheduledReport);
				}
			}
			catch (Throwable t) {
				log.error("Failed to request scheduled report", t);
				scheduledReport.setStatus(Status.FAILED);
				rs.saveReportRequest(scheduledReport);
			}
		}
	}
}
