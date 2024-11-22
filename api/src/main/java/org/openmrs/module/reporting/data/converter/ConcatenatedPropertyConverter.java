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