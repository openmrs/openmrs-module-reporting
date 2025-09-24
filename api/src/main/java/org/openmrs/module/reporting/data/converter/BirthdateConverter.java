/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import java.util.Locale;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Date data converter
 */
public class BirthdateConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private String exactDateFormat;
	private String estimatedDateFormat;
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
		this.exactDateFormat = ObjectUtil.nvlStr(exactDateFormat, "yyyy-MM-dd");
		this.estimatedDateFormat = (estimatedDateFormat == null ? this.exactDateFormat : estimatedDateFormat);
		this.locale = (locale == null ? Context.getLocale() : locale);
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a Birthdate into a String with the passed format
	 */
	public String convert(Object original) {
		Birthdate bd = (Birthdate) original;
		if (bd != null && bd.getBirthdate() != null) {
			if (bd.isEstimated()) {
				return ObjectUtil.format(bd.getBirthdate(), getEstimatedDateFormat());
			}
			else {
				return ObjectUtil.format(bd.getBirthdate(), getExactDateFormat());
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
	public String getExactDateFormat() {
		return exactDateFormat;
	}

	/**
	 * @param exactDateFormat the exactDateFormat to set
	 */
	public void setExactDateFormat(String exactDateFormat) {
		this.exactDateFormat = exactDateFormat;
	}

	/**
	 * @return the estimatedDateFormat
	 */
	public String getEstimatedDateFormat() {
		return estimatedDateFormat;
	}

	/**
	 * @param estimatedDateFormat the estimatedDateFormat to set
	 */
	public void setEstimatedDateFormat(String estimatedDateFormat) {
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