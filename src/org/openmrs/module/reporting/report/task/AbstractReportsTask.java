package org.openmrs.module.reporting.report.task;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.scheduler.SchedulerConstants;

/**
 * Generic superclass for a Reports task
 */
public abstract class AbstractReportsTask extends TimerTask {
	
	private static Log log = LogFactory.getLog(AbstractReportsTask.class);
	
	/**
	 * Sub-classes should override this method instead of the run method to implement their logic
	 * The run method takes care of exception handling and authentication to the Context for you
	 */
	public abstract void execute();
	
	/**
	 * @see TimerTask#run()
	 */
	@Override
	public final void run() {
		try {
			Context.openSession();
			if (!Context.isAuthenticated()) {
				authenticate();
			}
			execute();
		}
		catch (Exception e) {
			log.error("An error occurred while running scheduled reporting task", e);
		}
		finally {
			if (Context.isSessionOpen()) {
				Context.closeSession();
			}
		}
	}
	
	/**
	 * Authenticate the context so the task can call service layer.
	 */
	protected void authenticate() {
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String userName = adminService.getGlobalProperty(SchedulerConstants.SCHEDULER_USERNAME_PROPERTY);
			String password = adminService.getGlobalProperty(SchedulerConstants.SCHEDULER_PASSWORD_PROPERTY);
			Context.authenticate(userName, password);
		}
		catch (ContextAuthenticationException e) {
			log.error("Error authenticating user. Please ensure you scheduler username and password are configured correctly in your global properties", e);
		}
	}
}
