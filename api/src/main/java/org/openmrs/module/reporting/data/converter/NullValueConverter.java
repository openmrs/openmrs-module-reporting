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