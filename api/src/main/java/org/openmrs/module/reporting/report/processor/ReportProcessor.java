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
