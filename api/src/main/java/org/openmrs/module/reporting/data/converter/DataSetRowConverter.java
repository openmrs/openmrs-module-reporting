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
package org.openmrs.module.reporting.data.converter;

import org.openmrs.module.reporting.dataset.DataSetRow;

/**
 * List data converter
 */
public class DataSetRowConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private String columnName;
	
	//***** CONSTRUCTORS *****
	
	public DataSetRowConverter() { }
	
	/**
	 * Full Constructor
	 */
	public DataSetRowConverter(String columnName) {
		this.columnName = columnName;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a DataSetRow to the value in the column with the configured name
	 */
	public Object convert(Object original) {
		if (original != null) {
			DataSetRow dsr = (DataSetRow)original;
			return dsr.getColumnValue(getColumnName());
		}
		return original;
	}

	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return Object.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return DataSetRow.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}