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
			return Years.yearsBetween(birthDate.withTimeAtStartOfDay(), currentDate.withTimeAtStartOfDay()).getYears();
		}
		return null;
	}

	/**
	 * Return the age in full months
	 */
	public Integer getFullMonths() {
		if (birthDate != null) {
			return Months.monthsBetween(birthDate.withTimeAtStartOfDay(), currentDate.withTimeAtStartOfDay()).getMonths();
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
