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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of DataSetMetaData which contains a simple list of {@link DataSetColumn}
 */
public class SimpleDataSetMetaData implements DataSetMetaData {
	
	//***** PROPERTIES *****
	
	private List<DataSetColumn> columns;
	
	//***** CONSTRUCTORS *****
	
	public SimpleDataSetMetaData() {}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @return the number of columns in the DataSet
	 */
	public int getColumnCount() {
		return getColumns().size();
	}
	
	/**
	 * @return the column whose column name matches the passed String
	 */
	public DataSetColumn getColumn(String columnName) {
		for (DataSetColumn column : getColumns()) {
			if (column.getName().equalsIgnoreCase(columnName)) {
				return column;
			}
		}
		return null;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the columns
	 */
	public List<DataSetColumn> getColumns() {
		if (columns == null) {
			columns = new ArrayList<DataSetColumn>();
		}
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<DataSetColumn> columns) {
		this.columns = columns;
	}
	
	/**
	 * @param column the column to add
	 */
	public void addColumn(DataSetColumn column) {
		DataSetColumn existing = getColumn(column.getName());
		if (existing == null) {
			getColumns().add(column);
		}
	}
	
	/**
	 * @param column the column to remove
	 */
	public void removeColumn(DataSetColumn column) {
		getColumns().remove(column);
	}
	
	/**
	 * @param columnName the column to remove
	 */
	public void removeColumn(String columnName) {
		removeColumn(getColumn(columnName));
	}
}
