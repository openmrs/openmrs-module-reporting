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
