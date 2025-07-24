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

/**
 * Base converter, to provide a simple means of formatting Data values
 */
public interface DataConverter {
	
	/**
	 * Converter the passed object from one datatype to another
	 */
	public Object convert(Object original);
	
	/**
	 * @return the data type that this converters from
	 */
	public Class<?> getInputDataType();
	
	/**
	 * Return the datatype that this converters to
	 */
	public Class<?> getDataType();
}