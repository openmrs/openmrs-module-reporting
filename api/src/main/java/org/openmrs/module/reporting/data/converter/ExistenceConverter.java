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

/**
 * Data converter which provides a replacement value for non-null and null values
 */
public class ExistenceConverter implements DataConverter  {
	
	//***** PROPERTIES *****
	
	private Object notNullValue;
	private Object nullValue;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default constructor
	 */
	public ExistenceConverter() { }
	
	/**
	 * Full constructor
	 */
	public ExistenceConverter(Object notNullValue, Object nullValue) {
		this.notNullValue = notNullValue;
		this.nullValue = nullValue;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a Boolean to a configured text representation
	 */
	public Object convert(Object original) {
		return ObjectUtil.decode(original, nullValue, notNullValue);
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return Object.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Object.class;
	}
	
	//***** PROPERTIES *****

	/**
	 * @return the notNullValue
	 */
	public Object getNotNullValue() {
		return notNullValue;
	}

	/**
	 * @param notNullValue the notNullValue to set
	 */
	public void setNotNullValue(Object notNullValue) {
		this.notNullValue = notNullValue;
	}

	/**
	 * @return the nullValue
	 */
	public Object getNullValue() {
		return nullValue;
	}

	/**
	 * @param nullValue the nullValue to set
	 */
	public void setNullValue(Object nullValue) {
		this.nullValue = nullValue;
	}
}