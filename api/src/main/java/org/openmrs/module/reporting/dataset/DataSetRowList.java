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
 * Encapsulates a List of DataSetRows
 */
public class DataSetRowList extends ArrayList<DataSetRow> {
	
	public static final long serialVersionUID = 1L;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DataSetRowList() { } 

	/**
	 * @return the columns for this DataSetRow List
	 */
	public List<DataSetColumn> getColumns() {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		if (!isEmpty()) {
			l.addAll(get(0).getColumnValues().keySet());
		}
		return l;
	}
	
	/**
	 * @return the values for the particular Column
	 */
	public List<Object> getColumnValues(DataSetColumn column) {
		List<Object> l = new ArrayList<Object>();
		for (DataSetRow r : this) {
			l.add(r.getColumnValue(column));
		}
		return l;
	}
}