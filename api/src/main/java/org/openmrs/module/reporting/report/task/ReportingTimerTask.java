package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;

import java.util.TimerTask;

/**
 * Generic superclass for a Reports task
 */
public class ReportingTimerTask extends TimerTask {
	
	private final Log log = LogFactory.getLog(getClass());
	private static DaemonToken daemonToken;
	private ReportingTask task;

	//***** PROPERTIES THAT NEED TO BE SET ON EACH INSTANCE

	private Class<? extends ReportingTask> taskClass;
	private SessionFactory sessionFactory;

	/**
	 * @see TimerTask#run()
	 */
	@Override
	public final void run() {
		if (daemonToken != null) {
			createAndRunTask();
		}
		else {
			log.warn("Not running scheduled task as DaemonToken is null.");
		}
	}

	/**
	 * Construct a new instance of the configured task and execute it
	 */
	public void createAndRunTask() {
		try {
			log.info("Running reporting task: " + getTaskClass().getSimpleName());
			task = getTaskClass().newInstance();
			task.setScheduledExecutionTime(System.currentTimeMillis());
			task.setSessionFactory(sessionFactory);
			Daemon.runInDaemonThread(task, daemonToken);
		}
		catch (Exception e) {
			log.error("An error occurred while running scheduled reporting task", e);
		}
	}

	@Override
	public boolean cancel() {
		boolean ret = super.cancel();
		if (task != null) {
			task.cancelTask();
		}
		return ret;
	}

	public Class<? extends ReportingTask> getTaskClass() {
		return taskClass;
	}

	public void setTaskClass(Class<? extends ReportingTask> taskClass) {
		this.taskClass = taskClass;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Sets the daemon token
	 */
	public static void setDaemonToken(DaemonToken token) {
		daemonToken = token;
	}
}
