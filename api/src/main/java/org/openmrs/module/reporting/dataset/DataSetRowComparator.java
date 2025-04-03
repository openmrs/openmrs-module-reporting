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
