/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

/**
 * Represents comparisons that can be performed upon sets of unordered values, e.g. Text or Coded
 * @see RangeComparator
 */
public enum SetComparator {
	IN("IN"), NOT_IN("NOT IN");
	
	public final String sqlRep;
	
	SetComparator(String sqlRep) {
		this.sqlRep = sqlRep;
	}
	
	public String getSqlRepresentation() {
		return sqlRep;
	}
}