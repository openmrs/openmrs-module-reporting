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
package org.openmrs.module.reporting.data.converter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.ObjectUtil;

import java.util.Date;

/**
 * Converts a Birthdate into an Age
 */
public class AgeConverter implements DataConverter {
	
	public static String YEARS = "{y}";
	public static String YEARS_TO_ONE_DECIMAL_PLACE = "{y:1}";
	public static String MONTHS = "{m}";
	
	//***** PROPERTIES *****
	
	private String format;
	
	//***** CONSTRUCTORS *****
	
	public AgeConverter() {}
	
	/**
	 * Full Constructor
	 */
	public AgeConverter(String format) {
		this.format = format;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert an Age to integer years
	 * @should convert an Age to integer months
	 * @should convert an Age to a formatted string
	 */
	public Object convert(Object original) {
		Age age = (Age) original;
		String s = ObjectUtil.nvl(getFormat(), YEARS);
		if (age != null) {
			if (s.equals(MONTHS)) {
				return age.getFullMonths();
			}
			if (s.equals(YEARS)) {
				return age.getFullYears();
			}
            if (s.equals(YEARS_TO_ONE_DECIMAL_PLACE)) {
                return getYearsToOneDecimalPlace(age);
            }
            boolean containsYears = false;
			if (s.contains(YEARS)) {
				containsYears = true;
				s = s.replace(YEARS, ObjectUtil.nvlStr(age.getFullYears(), "0"));
			}
            if (s.contains(YEARS_TO_ONE_DECIMAL_PLACE)) {
                containsYears = true;
                s = s.replace(YEARS_TO_ONE_DECIMAL_PLACE, ObjectUtil.nvlStr(getYearsToOneDecimalPlace(age), "0"));
            }
            if (s.contains(MONTHS)) {
				int months = age.getFullMonths();
				if (containsYears && months > 12) {
					months = months % 12;
				}
				s = s.replace(MONTHS, ObjectUtil.nvlStr(months, "0"));
			}
			return s;
		}
		return "";
	}

    private Double getYearsToOneDecimalPlace(Age age) {
        if (age.getBirthDate() == null) {
            return null;
        }
        Days days = Days.daysBetween(
                new DateTime(age.getBirthDate().getTime()),
                new DateTime((age.getCurrentDate() == null ? new Date() : age.getCurrentDate()).getTime()));
        return Math.round(10d * (days.getDays() / 365.25d)) / 10d;
    }

    /**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		String s = ObjectUtil.nvl(getFormat(), YEARS);
		if (s.equals(MONTHS) || s.equals(YEARS)) {
			return Integer.class;
		}
		return String.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Age.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}