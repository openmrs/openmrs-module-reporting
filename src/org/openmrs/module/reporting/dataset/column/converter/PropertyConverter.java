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

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;

/**
 * Date column converter
 */
public class PropertyConverter<T> implements ColumnConverter {
	
	//***** PROPERTIES *****
	
	private String format;
	
	//***** CONSTRUCTORS *****
	
	public PropertyConverter() {}
	
	/**
	 * Full Constructor
	 */
	public PropertyConverter(String format) {
		this.format = format;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see ColumnConverter#converter(Object)
	 */
	public Object convert(Object o) {
		String propertyName = ObjectUtil.nvl(getFormat(), "");
		if (o != null) {
			if (ObjectUtil.isNull(propertyName)) {
				return o.toString();
			}
			return ReflectionUtil.getPropertyValue(o, propertyName);
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
		return Object.class;
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