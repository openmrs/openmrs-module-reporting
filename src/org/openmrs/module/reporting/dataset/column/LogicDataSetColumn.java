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
package org.openmrs.module.reporting.dataset.column;

/**
 * Implementation of logic rule backed dataset column.
 */
public class LogicDataSetColumn extends SimpleDataSetColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String logicQuery;

	
	/**
	 * Default Constructor
	 */
	public LogicDataSetColumn(String displayName, Class<?> dataType, String logicQuery) {
		super(displayName, displayName, null, dataType);
		this.logicQuery = logicQuery;
	} 	
	
	/**
	 * Default Constructor
	 */
	public LogicDataSetColumn(String columnKey, String displayName, String description, Class<?> dataType, String logicQuery) {
		super(columnKey, displayName, description, dataType);
		this.logicQuery = logicQuery;
	} 	
 
    /**
     * @return the logic query represented as a string
     */
    public String getLogicQuery() { 
    	return this.logicQuery;
    }

}