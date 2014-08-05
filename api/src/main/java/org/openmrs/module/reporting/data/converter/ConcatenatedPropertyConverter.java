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

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;

/**
 * Property data converter
 */
public class ConcatenatedPropertyConverter implements DataConverter {

	//***** PROPERTIES *****

	private String separator = " ";
	private String[] propertyNames;


	//***** CONSTRUCTORS *****

	public ConcatenatedPropertyConverter() {}

	/**
	 * Full Constructor
	 */
	public ConcatenatedPropertyConverter(String separator, String... propertyNames) {
		this.separator = separator;
		this.propertyNames = propertyNames;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object o) {
		StringBuilder ret = new StringBuilder();
		if (o != null) {
			for (String propertyName : propertyNames) {
				Object s = ReflectionUtil.getPropertyValue(o, propertyName);
				if (ObjectUtil.notNull(s)) {
					ret.append(ret.length() > 0 ? separator : "").append(s);
				}
			}
		}
		return ret.toString();
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}

	@Override
	public Class<?> getInputDataType() {
		return Object.class;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String[] getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(String[] propertyNames) {
		this.propertyNames = propertyNames;
	}
}