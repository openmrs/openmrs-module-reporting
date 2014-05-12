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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;

/**
 * Represents an Age as of a certain date
 */
public class Age {

	/**
	 * Units that the age can be returned in
	 */
	public enum Unit {
		YEARS, MONTHS
	}

	//***********************
	// PROPERTIES
	//***********************

	private DateTime birthDate;
	private DateTime currentDate;

	//***********************
	// CONSTRUCTORS
	//***********************

	/**
	 * Full Constructor
	 */
	public Age(Date birthDate) {
		this(birthDate, new Date());
	}

	/**
	 * Full Constructor
	 */
	public Age(Date birthDate, Date currentDate) {
		setBirthDate(birthDate);
		setCurrentDate(currentDate);
	}

	//***********************
	// INSTANCE METHODS
	//***********************

	/**
	 * Return the age in full years
	 */
	public Integer getFullYears() {
		if (birthDate != null) {
			return Years.yearsBetween(birthDate, currentDate).getYears();
		}
		return null;
	}

	/**
	 * Return the age in full months
	 */
	public Integer getFullMonths() {
		if (birthDate != null) {
			return Months.monthsBetween(birthDate, currentDate).getMonths();
		}
		return null;
	}

	/**
	 * Return the age in full months since last birthday
	 */
	public Integer getFullMonthsSinceLastBirthday() {
		if (birthDate != null) {
			int fullMonths = getFullMonths();
			return fullMonths % 12;
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Age)) {
			return false;
		}
		Age other = (Age) o;
		return OpenmrsUtil.nullSafeEquals(getBirthDate(), other.getBirthDate()) && OpenmrsUtil.nullSafeEquals(getCurrentDate(), other.getCurrentDate());
	}

	@Override
	public int hashCode() {
		return (getBirthDate() != null ? getBirthDate().hashCode() : 0) + (getCurrentDate() != null ? getCurrentDate().hashCode() : 0);
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ObjectUtil.nvlStr(getFullYears(), "");
	}

	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate.toDate();
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = new DateTime(birthDate);
	}

	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate.toDate();
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = new DateTime(currentDate);
	}
}
