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

/**
 * If there are any non-persisted reports in the cache, persist them.  
 * Remove oldest reports from Cache if max has been reached
 */
public class PersistCachedReportsTask extends ReportingTask {

	@Override
	public synchronized void executeTask() {
		getReportService().persistCachedReports();
	}
}
