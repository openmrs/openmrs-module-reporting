/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.report.task.ReportingTimerTask;
import org.openmrs.module.reporting.report.task.RunQueuedReportsTask;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class ReportingModuleActivator extends BaseModuleActivator implements DaemonTokenAware {

	private Log log = LogFactory.getLog(this.getClass());

    @Override
    public void contextRefreshed() {
		// This will ensure that the MessageUtil picks up the current active message source
		MessageUtil.setMessageSource(null);
    }

    @Override
	public void started() {
		ReportingTimerTask.setEnabled(true);
		log.info("Reporting Module Started...");
	}

	@Override
	public void willStop() {
		cancelAllScheduledTasks();
		cancelCurrentlyRunningReportRequests();
	}

	@Override
	public void stopped() {
		log.info("Reporting Module Stopped...");
	}

	@Override
	public void setDaemonToken(DaemonToken token) {
		ReportingTimerTask.setDaemonToken(token);
	}

	/**
	 * Cancels all scheduled tasks
	 */
	private void cancelAllScheduledTasks() {
		for (ReportingTimerTask task : Context.getRegisteredComponents(ReportingTimerTask.class)) {
			try {
				task.cancel();
			}
			catch (Exception e) {
				log.warn("An exception occurred while trying to stop reporting task " + task.getTaskClass().getSimpleName(), e);
			}
		}
	}

	/**
	 * Cancels all currently running report requests
	 */
	private void cancelCurrentlyRunningReportRequests() {
		for (RunQueuedReportsTask task : RunQueuedReportsTask.getCurrentlyRunningRequests().values()) {
			try {
				task.cancelTask();
			}
			catch (Exception e) {
				log.warn("An exception occurred while trying to stop currently running reports", e);
			}
		}
	}
}
