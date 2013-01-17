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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.module.reporting.report.task.AbstractReportsTask;
import org.openmrs.module.reporting.report.task.RunQueuedReportsTask;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class ModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting the Reporting Module ...");
	}
	
	/**
	 *  @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		List<AbstractReportsTask> tasks = Context.getRegisteredComponents(AbstractReportsTask.class);
		for (AbstractReportsTask task : tasks) {
			task.cancel(); //let's first cancel any future tasks
	        task.cancelCurrentlyRunningReportingTask(); //finally cancel running tasks
        }
		
		//Some report requests may be running in a task executor so we need to stop them as well.
		Map<String, RunQueuedReportsTask> runningRequests = RunQueuedReportsTask.getCurrentlyRunningRequests();
		for (AbstractReportsTask runningRequest : runningRequests.values()) {
	        runningRequest.cancelCurrentlyRunningReportingTask();
        }
		
		log.info("Shutting down the Reporting Module ...");
	}
	
}
