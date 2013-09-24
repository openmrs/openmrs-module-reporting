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
package org.openmrs.module.reporting.report.renderer;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.report.ReportDesign;

/**
 * Implementation of a ReportRenderer that renders ReportData to a TSV file
 */
@Handler
@Localized("reporting.TsvReportRenderer")
public class TsvReportRenderer extends DelimitedTextReportRenderer {
	
	/**
	 * Default Constructor
	 */
	public TsvReportRenderer() { }

	/**
	 * @return the filename extension for the particular type of delimited file
	 */
	@Override
	public String getFilenameExtension(ReportDesign design) {
		return design.getPropertyValue("filenameExtension", "tsv");
	}


	/**
	 * @return the delimiter that separates each column value
	 */
	@Override
	public String getFieldDelimiter(ReportDesign design) {
		return design.getPropertyValue("fieldDelimiter", "\t");
	}
}
