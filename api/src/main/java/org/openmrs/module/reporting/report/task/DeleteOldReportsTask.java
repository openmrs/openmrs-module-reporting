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
 * If there are any unsaved reports that are older than the expiration period set, delete them
 */
public class DeleteOldReportsTask extends ReportingTask {

	@Override
	public synchronized void executeTask() {
		getReportService().deleteOldReportRequests();
	}
}
