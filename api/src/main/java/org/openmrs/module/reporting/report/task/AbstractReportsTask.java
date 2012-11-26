package org.openmrs.module.reporting.report.task;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.scheduler.SchedulerConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generic superclass for a Reports task
 */
public abstract class AbstractReportsTask extends TimerTask {
	
	private final Log log = LogFactory.getLog(AbstractReportsTask.class);
	
	// Per REPORT-368, we need to avoid locking the admin user account if the scheduler.password GP is wrong.
	private static Date lastFailedLogin = null;
	
	private static SessionFactory sessionFactory;
	
	private volatile Session currentSession;
	
	/**
	 * Sub-classes should override this method instead of the run method to implement their logic
	 * The run method takes care of exception handling and authentication to the Context for you
	 */
	public abstract void execute();
	
	@Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
		//Store it in a static variable so that you can instantiate tasks with 'new'.
    	AbstractReportsTask.sessionFactory = sessionFactory;
    }

	/**
	 * @see TimerTask#run()
	 */
	@Override
	public final void run() {
		try {
			Context.openSession();
			currentSession = sessionFactory.getCurrentSession();
			
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
				Context.clearSession();
				Context.closeSession();
			}
		}
	}
	
	public void cancelCurrentlyRunnningReportingTask() {
		Session session = currentSession;
		if (session != null && session.isOpen()) {
			session.close();
			log.info("Reporting task has been cancelled");
		} else {
			log.warn("Failed to cancel the reporting task");
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
			log.warn("Error authenticating '"
			        + userName
			        + "' user. Please ensure you scheduler username and password are configured correctly in your global properties");
			lastFailedLogin = new Date();
		}
	}
}
