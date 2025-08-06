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