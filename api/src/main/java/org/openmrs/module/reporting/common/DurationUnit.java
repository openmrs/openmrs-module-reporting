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

import java.util.Calendar;

/**
 * Represents a duration unit
 */
public enum DurationUnit {
	
	SECONDS	(Calendar.SECOND, 1),
	MINUTES	(Calendar.MINUTE, 1),
	HOURS	(Calendar.HOUR, 1),
	DAYS	(Calendar.DATE, 1),
	WEEKS	(Calendar.DATE, 7),
	MONTHS	(Calendar.MONTH, 1),
	YEARS	(Calendar.YEAR, 1);
	
	/**
	 * Constructor
	 */
	DurationUnit(Integer calendarField, Integer fieldQuantity) {
		this.calendarField = calendarField;
		this.fieldQuantity = fieldQuantity;
	}
	
	/**
	 * Property representing the Calendar field
	 */
	private final Integer calendarField;
	
	/**
	 * Property representing the field quantity
	 */
	private final Integer fieldQuantity;
	
	/**
	 * Return the calendar field
	 * @return
	 */
	public Integer getCalendarField() {
		return calendarField;
	}
	
	/**
	 * Return the calendar field quantity
	 * @return
	 */
	public Integer getFieldQuantity() {
		return fieldQuantity;
	}
}
