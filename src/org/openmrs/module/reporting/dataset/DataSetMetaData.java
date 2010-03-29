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

import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * This interface is meant to provide access to information about the Columns in a DataSet
 * It is loosely based on {@link ResultSetMetaData} and org.eclipse.datatools.connectivity.oda.IResultSetMetaData class
 */
public interface DataSetMetaData {
	
	/**
	 * @return the number of columns in the DataSet
	 */
	public int getColumnCount();
	
	/**
	 * @return the column whose column key matches the passed String
	 */
	public DataSetColumn getColumn(String columnName);
	
	/**
	 * @return a List of all columns
	 */
	public List<DataSetColumn> getColumns();
}
