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

import org.openmrs.PersonName;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Date data converter
 */
public class PersonNameConverter implements DataConverter {
	
	public static String PREFIX = "{p}";
	public static String GIVEN_NAME = "{gn}";
	public static String MIDDLE_NAME = "{mn}";
	public static String FAMILY_NAME_PREFIX = "{fnp}";
	public static String FAMILY_NAME = "{fn}";
	public static String FAMILY_NAME_2 = "{fn2}";
	public static String FAMILY_NAME_SUFFIX = "{fns}";
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
	 * @see DataConverter#converter(Object)
	 * @should convert a Person name into a String using a format expression
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
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
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