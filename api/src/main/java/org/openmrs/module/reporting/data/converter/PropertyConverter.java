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