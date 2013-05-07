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
 * Formats any object into a String representation
 */
public class ObjectFormatter implements DataConverter {

	// ***** PROPERTIES *****

	private String specification;

	// ***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public ObjectFormatter() {
	}

	/**
	 * Constructor with specification
	 */
	public ObjectFormatter(String specification) {
		this.specification = specification;
	}

	// ***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#converter(Object)
	 * @should convert an Object into a nicely formatted text representation
	 */
	public Object convert(Object o) {
		try {
			return ObjectUtil.format(o, getSpecification());
		} catch (Exception e) {
			throw new ConversionException(
					"Unable to convert Object "
							+ o
							+ " into a nicely formatted text representation,with the specification: "
							+ getSpecification() + " due to: " + e, e);
		}
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
		return Object.class;
	}

	// ***** PROPERTY ACCESS *****

	/**
	 * @return the specification
	 */
	public String getSpecification() {
		return specification;
	}

	/**
	 * @param specification
	 *            the specification to set
	 */
	public void setSpecification(String specification) {
		this.specification = specification;
	}
}