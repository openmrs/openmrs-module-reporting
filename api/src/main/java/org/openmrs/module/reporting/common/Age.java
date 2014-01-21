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

import org.openmrs.util.OpenmrsUtil;

import java.util.Calendar;
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
	
	private Date birthDate;
	private Date currentDate;
	
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
		this.birthDate = birthDate;
		this.currentDate = currentDate;
	}
	
	//***********************
	// INSTANCE METHOS
	//***********************
	
	/**
	 * Return the age in full years
	 */
	public Integer getFullYears() {
		Integer age = null;
		if (birthDate != null) {
			Calendar today = Calendar.getInstance();
			today.setTime(ObjectUtil.nvl(currentDate, new Date()));
			Calendar bday = Calendar.getInstance();
			bday.setTime(birthDate);
			
			age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);
			
			// Adjust age when today's date is before the person's birthday
			int todaysMonth = today.get(Calendar.MONTH);
			int bdayMonth = bday.get(Calendar.MONTH);
			int todaysDay = today.get(Calendar.DAY_OF_MONTH);
			int bdayDay = bday.get(Calendar.DAY_OF_MONTH);
			
			if (todaysMonth < bdayMonth || (todaysMonth == bdayMonth && todaysDay < bdayDay)) {
				age--;
			}
		}
		return age;
	}
	
	/**
	 * Return the age in full months
	 */
	public Integer getFullMonths() {
		if (birthDate != null) {
			return getFullYears() * 12 + getFullMonthsSinceLastBirthday();
		}
		return null;
	}
	
	/**
	 * Return the age in full months since last birthday
	 */
	public Integer getFullMonthsSinceLastBirthday() {
		Integer age = null;
		if (birthDate != null) {
			Calendar today = Calendar.getInstance();
			today.setTime(ObjectUtil.nvl(currentDate, new Date()));
			Calendar bday = Calendar.getInstance();
			bday.setTime(birthDate);

			age = today.get(Calendar.MONTH) - bday.get(Calendar.MONTH);
			if (age < 0) {
				age += 12;
			}
			
			// Adjust age when today's date is before the person's birthday
			int todaysDay = today.get(Calendar.DAY_OF_MONTH);
			int bdayDay = bday.get(Calendar.DAY_OF_MONTH);
			
			if (todaysDay < bdayDay) {
				age--;
			}
		}
		return age;
	}

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Age)) {
            return false;
        }
        Age other = (Age) o;
        return OpenmrsUtil.nullSafeEquals(birthDate, other.birthDate) && OpenmrsUtil.nullSafeEquals(currentDate, other.currentDate);
    }

    @Override
    public int hashCode() {
        return (birthDate != null ? birthDate.hashCode() : 0) + (currentDate != null ? currentDate.hashCode() : 0);
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
		return birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
}
