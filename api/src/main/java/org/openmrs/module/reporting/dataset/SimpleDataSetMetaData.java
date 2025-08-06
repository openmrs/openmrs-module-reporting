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
