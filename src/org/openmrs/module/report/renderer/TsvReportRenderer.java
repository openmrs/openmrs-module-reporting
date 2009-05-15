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
package org.openmrs.module.report.renderer;

import org.openmrs.annotation.Handler;
import org.openmrs.module.report.ReportSchema;

/**
 * Implementation of a ReportRenderer that renders ReportData to a TSV file
 */
@Handler
public class TsvReportRenderer extends DelimitedTextReportRenderer {
	
	/**
	 * Default Constructor
	 */
	public TsvReportRenderer() { }
	
	/**
	 * @see DelimitedTextReportRenderer#getLabel()
	 */
	public String getLabel() {
		return "TSV";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getFilenameExtension()
	 */
	public String getFilenameExtension() {
		return "tsv";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getBeforeColumnDelimiter()
	 */
	public String getBeforeColumnDelimiter() {
		return "\"";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getAfterColumnDelimiter()
	 */
	public String getAfterColumnDelimiter() {
		return "\"\t";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getBeforeRowDelimiter()
	 */
	public String getBeforeRowDelimiter() {
		return "";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getAfterRowDelimiter()
	 */
	public String getAfterRowDelimiter() {
		return "\n";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#escape(String)
	 */
	public String escape(String text) {
		if (text == null) {
			return null;
		}
		else {
			return text.replaceAll("\"", "\\\"");
		}
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderedContentType(ReportSchema, String)
	 */
	public String getRenderedContentType(ReportSchema model, String argument) {
		return "text/tsv";
	}
}
