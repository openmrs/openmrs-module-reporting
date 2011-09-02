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

import org.openmrs.PersonName;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Date column converter
 */
public class PersonNameConverter implements ColumnConverter {
	
	public static String PREFIX = "{p}";
	public static String GIVEN_NAME = "{g}";
	public static String MIDDLE_NAME = "{m}";
	public static String FAMILY_NAME_PREFIX = "{fp}";
	public static String FAMILY_NAME = "{f}";
	public static String FAMILY_NAME_2 = "{f2}";
	public static String FAMILY_NAME_SUFFIX = "{fs}";
	public static String DEGREE = "{d}";
	
	//***** PROPERTIES *****
	
	private String format;
	
	//***** CONSTRUCTORS *****
	
	public PersonNameConverter() {}
	
	/**
	 * Full Constructor
	 */
	public PersonNameConverter(String format) {
		this.format = format;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see ColumnConverter#converter(Object)
	 */
	public Object convert(Object original) {
		PersonName name = (PersonName) original;
		String s = ObjectUtil.nvl(getFormat(), "");
		if (name != null) {
			if (ObjectUtil.isNull(format)) {
				return name.toString();
			}
			if (s.contains(PREFIX) && ObjectUtil.notNull(name.getPrefix())) {
				s = s.replace(PREFIX, name.getPrefix());
			}
			if (s.contains(GIVEN_NAME) && ObjectUtil.notNull(name.getGivenName())) {
				s = s.replace(GIVEN_NAME, name.getGivenName());
			}
			if (s.contains(MIDDLE_NAME) && ObjectUtil.notNull(name.getMiddleName())) {
				s = s.replace(MIDDLE_NAME, name.getMiddleName());
			}
			if (s.contains(FAMILY_NAME_PREFIX) && ObjectUtil.notNull(name.getFamilyNamePrefix())) {
				s = s.replace(FAMILY_NAME_PREFIX, name.getFamilyNamePrefix());
			}
			if (s.contains(FAMILY_NAME) && ObjectUtil.notNull(name.getFamilyName())) {
				s = s.replace(FAMILY_NAME, name.getFamilyName());
			}
			if (s.contains(FAMILY_NAME_2) && ObjectUtil.notNull(name.getFamilyName2())) {
				s = s.replace(FAMILY_NAME_2, name.getFamilyName2());
			}
			if (s.contains(FAMILY_NAME_SUFFIX) && ObjectUtil.notNull(name.getFamilyNameSuffix())) {
				s = s.replace(FAMILY_NAME_SUFFIX, name.getFamilyNameSuffix());
			}
			if (s.contains(DEGREE) && ObjectUtil.notNull(name.getDegree())) {
				s = s.replace(DEGREE, name.getDegree());
			}
		}
		return s;
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
		return PersonName.class;
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