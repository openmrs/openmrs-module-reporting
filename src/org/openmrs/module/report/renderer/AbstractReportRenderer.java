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

import java.util.List;

/**
 * Abstract implementation of a ReportRenderer.
 */
public abstract class AbstractReportRenderer implements ReportRenderer  {
	
	private List<String> displayColumns;
	
	/**
	 * Default Constructor
	 */
	public AbstractReportRenderer() { }

	
	public void setDisplayColumns(List<String> displayColumns) { 
		this.displayColumns = displayColumns;
	}

	public List<String> getDisplayColumns() { 
		return displayColumns;
	}
	
	/**
	 * 
	 * @param column
	 * @return
	 */
	protected boolean isDisplayColumn(String column) { 
		// Check if the given column is specified as a display column
		if (displayColumns != null && !displayColumns.isEmpty())
			return displayColumns.contains(column);		
		
		// If display columns aren't specified, the given column should be displayed
		return true;
	}
	
	
}
