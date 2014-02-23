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

/**
 * Data converter which provides a replacement value null values
 */
public class NullValueConverter implements DataConverter  {

	//***** PROPERTIES *****

	private Object nullReplacement;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public NullValueConverter() { }

	/**
	 * Full constructor
	 */
	public NullValueConverter(Object nullReplacement) {
		this.nullReplacement = nullReplacement;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#convert(Object)
	 * @should convert a null value to the configured replacement value
	 */
	public Object convert(Object original) {
		return ObjectUtil.nvl(original, nullReplacement);
	}

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return nullReplacement.getClass();
	}

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Object.class;
	}
	
	//***** PROPERTIES *****

	public Object getNullReplacement() {
		return nullReplacement;
	}

	public void setNullReplacement(Object nullReplacement) {
		this.nullReplacement = nullReplacement;
	}
}