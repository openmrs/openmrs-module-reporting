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
 * Data converter which provides a replacement value for non-null and null
 * values
 */
public class ExistenceConverter implements DataConverter {

	// ***** PROPERTIES *****

	private Object notNullValue;
	private Object nullValue;

	// ***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public ExistenceConverter() {
	}

	/**
	 * Full constructor
	 */
	public ExistenceConverter(Object notNullValue, Object nullValue) {
		this.notNullValue = notNullValue;
		this.nullValue = nullValue;
	}

	// ***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#converter(Object)
	 * @should convert a Boolean to a configured text representation
	 */
	public Object convert(Object original) {
		try {
			return ObjectUtil.decode(original, nullValue, notNullValue);
		} catch (Exception e) {
			throw new ConversionException(
					"Unable to convert Boolean"
							+ original
							+ "to a configured text representation, which provides a replacement value for non-null and null values, due to: "
							+ e, e);
		}
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

	// ***** PROPERTIES *****

	/**
	 * @return the notNullValue
	 */
	public Object getNotNullValue() {
		return notNullValue;
	}

	/**
	 * @param notNullValue
	 *            the notNullValue to set
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
	 * @param nullValue
	 *            the nullValue to set
	 */
	public void setNullValue(Object nullValue) {
		this.nullValue = nullValue;
	}
}