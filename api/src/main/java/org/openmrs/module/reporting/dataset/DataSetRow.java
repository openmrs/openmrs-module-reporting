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
