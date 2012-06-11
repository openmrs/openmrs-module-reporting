package org.openmrs.module.reporting.report.task;

import java.util.Date;
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
	
	// Per REPORT-368, we need to avoid locking the admin user account if the scheduler.password GP is wrong.
	private static Date lastFailedLogin = null;
	
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
			log.debug("Running reporting task: " + getClass().getSimpleName());
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
		// Avoid locking the admin user account if the scheduler.password GP is wrong. 
		// (The number 300000 is hardcoded in HibernateContextDAO in core as of 1.9. If that changes, we need to
		// update this check. I add 5 seconds to that value to be sure we're safely out of the locking window.) 
		if (lastFailedLogin != null && (System.currentTimeMillis() - lastFailedLogin.getTime() < 305000)) {
			log.warn("Not authenticating to avoid locking account. Please ensure you scheduler username and password are configured correctly in your global properties");
			return;
		}
		
		AdministrationService adminService = Context.getAdministrationService();
		String userName = adminService.getGlobalProperty(SchedulerConstants.SCHEDULER_USERNAME_PROPERTY);
		String password = adminService.getGlobalProperty(SchedulerConstants.SCHEDULER_PASSWORD_PROPERTY);
		try {
			Context.authenticate(userName, password);
		}
		catch (ContextAuthenticationException e) {
			log.warn("Error authenticating '" + userName + "' user. Please ensure you scheduler username and password are configured correctly in your global properties");
			lastFailedLogin = new Date();
		}
	}
}
