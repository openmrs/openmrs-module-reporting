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