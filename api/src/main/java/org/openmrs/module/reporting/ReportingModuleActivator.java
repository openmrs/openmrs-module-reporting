/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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
		// This will ensure that the MessageUtil picks back up the current active message source
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
