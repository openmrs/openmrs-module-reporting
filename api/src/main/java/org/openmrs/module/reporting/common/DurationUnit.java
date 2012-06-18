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
