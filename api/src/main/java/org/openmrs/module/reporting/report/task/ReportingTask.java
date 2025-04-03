/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.db.hibernate.DbSession;  
import org.openmrs.api.db.hibernate.DbSessionFactory;  
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * If there are any unsaved reports that are older than the expiration period set, delete them
 */
public abstract class ReportingTask implements Runnable {

	private transient final Log log = LogFactory.getLog(getClass());
	private volatile DbSession currentSession;

	// Properties that should be set on this task when it is instantiated, before it is run

	private DbSessionFactory sessionFactory;
	private long scheduledExecutionTime;

	@Override
	public void run() {
		try {
			log.info("Running reporting task: " + getClass().getSimpleName());
			currentSession = sessionFactory.getCurrentSession();
			executeTask();
			log.info("Completed reporting task: " + getClass().getSimpleName());
		}
		catch (Exception e) {
			log.error("An error occurred while running reporting task: " + getClass(), e);
		}
	}

	/**
	 * Subclasses should implement this method with the actual logic for this task
	 */
	protected abstract void executeTask();

	/**
	 * Attempt to cancel the currently running task by closing the hibernate session
	 */
	public void cancelTask() {
		log.info("Attempting to cancel " + getClass().getSimpleName());
		DbSession session = currentSession;
		if (session != null && session.isOpen()) {
			log.info(getClass().getSimpleName() + " task has been cancelled");
		}
		else {
			log.info("Did not have to cancel " + getClass().getSimpleName() + " task as it was not running");
		}
	}

	public long getScheduledExecutionTime() {
		return scheduledExecutionTime;
	}

	public void setScheduledExecutionTime(long scheduledExecutionTime) {
		this.scheduledExecutionTime = scheduledExecutionTime;
	}

	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected ReportService getReportService() {
		return Context.getService(ReportService.class);
	}
}
