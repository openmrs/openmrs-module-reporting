/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.Report;
import org.springframework.stereotype.Component;

/**
 * A basic ReportProcessor as a test which just logs to file
 */
@Component
public class LoggingReportProcessor implements ReportProcessor {
	
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */
	public List<String> getConfigurationPropertyNames() {
		return new ArrayList<String>();
	}

	/**
	 * Performs some action on the given report
	 * @param report the Report to process
	 */
	public void process(Report report, Properties configuration) {
		log.warn("Processing report with configuration: " + configuration);
		log.warn("Request: " + report.getRequest());
		log.warn("Number of Data Sets produced: " + report.getReportData().getDataSets().size());
		if (report.getRenderedOutput() != null) {
			log.warn("Rendered output produced of size: " + report.getRenderedOutput().length);
		}
		if (report.getErrorMessage() != null) {
			log.warn("An error occurred: " + report.getErrorMessage());
		}
	}
}
