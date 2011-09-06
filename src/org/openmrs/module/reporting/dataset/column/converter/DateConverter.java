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
package org.openmrs.module.reporting.dataset.column.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Date column converter
 */
public class DateConverter implements ColumnConverter {
	
	//***** PROPERTIES *****
	
	private DateFormat df;
	
	//***** CONSTRUCTORS *****
	
	public DateConverter() {
		this("yyyy-MM-dd");
	}
	
	/**
	 * Constructor for just format, defaults to English Locale
	 */
	public DateConverter(String format) {
		this (format, Locale.ENGLISH);
	}
	
	/**
	 * Full Constructor
	 */
	public DateConverter(String format, Locale locale) {
		if (ObjectUtil.isNull(format)) {
			format = "yyyy-MM-dd";
		}
		df = new SimpleDateFormat(format, locale);
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see ColumnConverter#converter(Object)
	 * @should convert a Date into a String with the passed format
	 */
	public String convert(Object original) {
		Date date = (Date) original;
		if (date != null) {
			return df.format(date);
		}
		return "";
	}

	/** 
	 * @see ColumnConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	/** 
	 * @see ColumnConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Date.class;
	}
}