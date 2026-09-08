/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
