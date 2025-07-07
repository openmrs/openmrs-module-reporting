/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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