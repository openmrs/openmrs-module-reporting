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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;

/**
 * Date data converter
 */
public class DateConverter implements DataConverter {

	// ***** PROPERTIES *****

	private DateFormat dateFormat;
	private String conversionCalculation;

	// ***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public DateConverter() {
	}

	/**
	 * Constructor for just format
	 */
	public DateConverter(String format) {
		this(format, null);
	}

	/**
	 * String format and conversionCalculation
	 */
	public DateConverter(String format, String conversionCalculation) {
		this(new SimpleDateFormat(format), conversionCalculation);
	}

	/**
	 * Full Constructor
	 */
	public DateConverter(DateFormat dateFormat, String conversionCalculation) {
		this.dateFormat = dateFormat;
		this.conversionCalculation = conversionCalculation;
	}

	// ***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#converter(Object)
	 * @should convert a Date into a String with the passed format
	 */
	public Object convert(Object original) {
		Date date = (Date) original;
		try {
			if (date != null) {
				if (ObjectUtil.notNull(conversionCalculation)) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("date", date);
					date = (Date) EvaluationUtil.evaluateParameterExpression(
							"date" + conversionCalculation, m);
				}
				if (dateFormat != null) {
					return dateFormat.format(date);
				}
			}
		} catch (Exception e) {
			throw new ConversionException("Unable to convert Date" + original
					+ "into a String with the passed format, due to: " + e, e);
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
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat
	 *            the dateFormat to set
	 */
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the conversionCalculation
	 */
	public String getConversionCalculation() {
		return conversionCalculation;
	}

	/**
	 * @param conversionCalculation
	 *            the conversionCalculation to set
	 */
	public void setConversionCalculation(String conversionCalculation) {
		this.conversionCalculation = conversionCalculation;
	}
}