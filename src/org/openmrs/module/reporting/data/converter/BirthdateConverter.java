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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Date data converter
 */
public class BirthdateConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private DateFormat exactDateFormat;
	private DateFormat estimatedDateFormat;
	private Locale locale;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public BirthdateConverter() {
		this("yyyy-MM-dd");
	}
	
	/**
	 * Single Format Constructor
	 */
	public BirthdateConverter(String dateFormat) {
		this(dateFormat, dateFormat);
	}
	
	/**
	 * Constructor for just format, defaults to English Locale
	 */
	public BirthdateConverter(String exactDateFormat, String estimatedDateFormat) {
		this(exactDateFormat, estimatedDateFormat, null);
	}
	
	/**
	 * Full Constructor
	 */
	public BirthdateConverter(String exactDateFormat, String estimatedDateFormat, Locale locale) {
		this.exactDateFormat = new SimpleDateFormat(ObjectUtil.nvlStr(exactDateFormat, "yyyy-MM-dd"));
		this.estimatedDateFormat = (estimatedDateFormat == null ? this.exactDateFormat : new SimpleDateFormat(estimatedDateFormat));
		this.locale = (locale == null ? Context.getLocale() : locale);
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert a Birthdate into a String with the passed format
	 */
	public String convert(Object original) {
		Birthdate bd = (Birthdate) original;
		if (bd != null && bd.getBirthdate() != null) {
			if (bd.isEstimated()) {
				return getEstimatedDateFormat().format(bd.getBirthdate());
			}
			else {
				return getExactDateFormat().format(bd.getBirthdate());
			}
		}
		return "";
	}

	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Birthdate.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the exactDateFormat
	 */
	public DateFormat getExactDateFormat() {
		return exactDateFormat;
	}

	/**
	 * @param exactDateFormat the exactDateFormat to set
	 */
	public void setExactDateFormat(DateFormat exactDateFormat) {
		this.exactDateFormat = exactDateFormat;
	}

	/**
	 * @return the estimatedDateFormat
	 */
	public DateFormat getEstimatedDateFormat() {
		return estimatedDateFormat;
	}

	/**
	 * @param estimatedDateFormat the estimatedDateFormat to set
	 */
	public void setEstimatedDateFormat(DateFormat estimatedDateFormat) {
		this.estimatedDateFormat = estimatedDateFormat;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}