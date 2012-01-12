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
