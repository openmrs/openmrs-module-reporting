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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;

/**
 * Date data converter
 */
public class DateConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private String dateFormat;
	private Locale locale;
	private String conversionCalculation;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DateConverter() {}

	/**
	 * Constructor for just format
	 */
	public DateConverter(String dateFormat) {
		this(dateFormat, null);
	}
	
	/**
	 * String format and conversionCalculation
	 */
	public DateConverter(String dateFormat, String conversionCalculation) {
		this.dateFormat = dateFormat;
		this.conversionCalculation = conversionCalculation;
	}

	/**
	 * String format and conversionCalculation
	 */
	public DateConverter(String dateFormat, String conversionCalculation, Locale locale) {
		this.dateFormat = dateFormat;
		this.conversionCalculation = conversionCalculation;
		this.locale = locale;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a Date into a String with the passed format
	 */
	public Object convert(Object original) {
		Date date = (Date) original;
		if (date != null) {
			if (ObjectUtil.notNull(conversionCalculation)) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("date", date);
				date = (Date)EvaluationUtil.evaluateParameterExpression("date"+conversionCalculation, m);
			}
			if (dateFormat != null) {
				DateFormat df = (locale == null ? new SimpleDateFormat(dateFormat) : new SimpleDateFormat(dateFormat, locale));
				return df.format(date);
			}
		}
		return date;
	}

	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		if (dateFormat != null) {
			return String.class;
		}
		return Date.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Date.class;
	}

	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the conversionCalculation
	 */
	public String getConversionCalculation() {
		return conversionCalculation;
	}

	/**
	 * @param conversionCalculation the conversionCalculation to set
	 */
	public void setConversionCalculation(String conversionCalculation) {
		this.conversionCalculation = conversionCalculation;
	}
}