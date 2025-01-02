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

import java.util.Map;

/**
 * Collection converter
 */
public class MapElementConverter implements DataConverter {

	//***** PROPERTIES *****

	private Object key;
	private DataConverter valueConverter;

	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public MapElementConverter() { }

	/**
	 * Full Constructor
	 */
	public MapElementConverter(Object key, DataConverter valueConverter) {
		this.key = key;
		this.valueConverter = valueConverter;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#convert(Object)
	 * @should convert a Map into a String
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object original) {
		if (original != null) {
			Map<Object, Object> m = (Map<Object, Object>)original;
			Object item = m.get(key);
			if (item != null) {
				if (valueConverter != null) {
					return valueConverter.convert(item);
				}
			}
			return item;
		}
		return null;
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
		return Map.class;
	}
	
	//***** PROPERTY ACCESS *****

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public DataConverter getValueConverter() {
		return valueConverter;
	}

	public void setValueConverter(DataConverter valueConverter) {
		this.valueConverter = valueConverter;
	}
}