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

import java.util.Comparator;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.common.SortCriteria.SortElement;

/**
 * Data Set Row Comparator that uses sort criteria and applies these to the column values
 */
public class DataSetRowComparator implements Comparator<DataSetRow> {
	
	//***** PROPERTIES *****
	
	private SortCriteria sortCriteria;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DataSetRowComparator() { }
	
	/**
	 * Full Constructor
	 */
	public DataSetRowComparator(SortCriteria sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(DataSetRow r1, DataSetRow r2) {
		for (SortElement e : getSortCriteria().getSortElements()) {
			Object v1 = r1.getColumnValue(e.getElementName());
			Object v2 = r2.getColumnValue(e.getElementName());
			int result = 0;
			if (e.getDirection() == null || e.getDirection() == SortDirection.ASC) {
				result = ObjectUtil.nullSafeCompare(v1, v2);
			}
			else {
				result = ObjectUtil.nullSafeCompare(v2, v1);
			}
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	//***** PROPERTY ACCESS *****

	/**
	 * @return the sortCriteria
	 */
	public SortCriteria getSortCriteria() {
		return sortCriteria;
	}

	/**
	 * @param sortCriteria the sortCriteria to set
	 */
	public void setSortCriteria(SortCriteria sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
}
