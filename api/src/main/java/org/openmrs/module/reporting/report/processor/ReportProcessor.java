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

import java.util.List;
import java.util.Properties;

import org.openmrs.module.reporting.report.Report;

/**
 * A ReportProcessor which can perform a set of actions against Report
 */
public interface ReportProcessor {
	
	/**
	 * @return an array of all supported Configuration Properties
	 */
	public List<String> getConfigurationPropertyNames();

	/**
	 * Performs some action on the given report
	 * @param report the Report to process
	 */
	public void process(Report report, Properties configuration);
}
