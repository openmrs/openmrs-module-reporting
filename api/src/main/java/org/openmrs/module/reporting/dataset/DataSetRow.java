/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents one row of data in a {@link DataSet}
 */
public class DataSetRow implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//****** PROPERTIES ******
	
	private Map<DataSetColumn, Object> columnValues;
	
	/**
	 * Default Constructor
	 */
	public DataSetRow() { } 
	
	/**
	 * Retrieves the value for the row given the passed column
	 * @param column
	 */
	public Object getColumnValue(DataSetColumn column) {
		return getColumnValues().get(column);
	}
	
	/**
	 * Retrieves the value for the row given the passed column name
	 * @param columnName
	 */
	public Object getColumnValue(String columnName) {
		for (Map.Entry<DataSetColumn, Object> e : getColumnValues().entrySet()) {
			if (e.getKey().getName().equalsIgnoreCase(columnName)) {
				return e.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Adds a column value to the row
	 * @param column
	 * @param value
	 */
	public void addColumnValue(DataSetColumn column, Object value) {
		getColumnValues().put(column, value);
	}
	
	/**
	 * @return a map from key to value.
	 * Normally you would use {@link #getColumnValues()} but this version is useful if you need
	 * to use the returned map in JSTL for example. 
	 */
	public Map<String, Object> getColumnValuesByKey() {
		Map<String, Object> ret = new HashMap<String, Object>();
		if (columnValues != null) {
			for (Map.Entry<DataSetColumn, Object> e : columnValues.entrySet()) {
		        ret.put(e.getKey().getName(), e.getValue());
	        }
		}
		return ret;
	}
	
	/**
	 * Removes an entire column and all values from a DataSetRow
	 */
	public void removeColumn(String columnName) {
		for (Iterator<DataSetColumn> i = getColumnValues().keySet().iterator(); i.hasNext();) {
			DataSetColumn c = i.next();
			if (c.getName().equals(columnName)) {
				i.remove();
			}
		}
	}
	
	//****** PROPERTY ACCESS ******

	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<DataSetColumn> i = getColumnValues().keySet().iterator(); i.hasNext();) {
			DataSetColumn c = i.next();
			sb.append(c.getName() + "=" + getColumnValue(c) + (i.hasNext() ? ", " : ""));
		}
		return sb.toString();
	}

	/**
	 * @return the columnValues
	 */
	public Map<DataSetColumn, Object> getColumnValues() {
		if (columnValues == null) {
			columnValues = new LinkedHashMap<DataSetColumn, Object>();
		}
		return columnValues;
	}

	/**
	 * @param columnValues the columnValues to set
	 */
	public void setColumnValues(Map<DataSetColumn, Object> columnValues) {
		this.columnValues = columnValues;
	}
}
