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
public class PropertyConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private Class<?> typeToConvert;
	private String propertyName;
	
	//***** CONSTRUCTORS *****
	
	public PropertyConverter() {}
	
	/**
	 * Full Constructor
	 */
	public PropertyConverter(Class<?> typeToConvert, String propertyName) {
		this.typeToConvert = typeToConvert;
		this.propertyName = propertyName;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert an Object into it's property whose name is the configured propertyName
	 * @should convert an Object into it's string representation if not propertyName is configured
	 */
	public Object convert(Object o) {
		String propertyName = ObjectUtil.nvl(getPropertyName(), "");
		if (o != null) {
			if (ObjectUtil.isNull(propertyName)) {
				return o.toString();
			}
			return ReflectionUtil.getPropertyValue(o, propertyName);
		}
		return null;
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		if (typeToConvert != null) {
			if (propertyName != null) {
				return ReflectionUtil.getPropertyType(typeToConvert, getPropertyName());
			}
		}
		else {
			if (propertyName != null) {
				return Object.class;
			}
		}
		return String.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return typeToConvert;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the typeToConvert
	 */
	public Class<?> getTypeToConvert() {
		return typeToConvert;
	}

	/**
	 * @param typeToConvert the typeToConvert to set
	 */
	public void setTypeToConvert(Class<?> typeToConvert) {
		this.typeToConvert = typeToConvert;
	}
	
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
}