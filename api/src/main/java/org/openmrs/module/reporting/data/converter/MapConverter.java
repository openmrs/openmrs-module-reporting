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

import java.util.Map;

/**
 * Collection converter
 */
public class MapConverter implements DataConverter {

	//***** PROPERTIES *****

	private String keyValueSeparator;
	private String entrySeparator;
	private DataConverter keyConverter;
	private DataConverter valueConverter;
	private boolean includeNullValues;

	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public MapConverter() { }

	/**
	 * Full Constructor
	 */
	public MapConverter(String keyValueSeparator, String entrySeparator, DataConverter keyConverter, DataConverter valueConverter) {
		this.keyValueSeparator = keyValueSeparator;
		this.entrySeparator = entrySeparator;
		this.keyConverter = keyConverter;
		this.valueConverter = valueConverter;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#convert(Object)
	 * @should convert a Map into a String
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object original) {
		if (original != null) {
			StringBuilder ret = new StringBuilder();
			for (Map.Entry<Object, Object> e : ((Map<Object, Object>) original).entrySet()) {
				Object key = e.getKey();
				if (keyConverter != null) {
					key = keyConverter.convert(key);
				}
				Object val = e.getValue();
				if (valueConverter != null) {
					val = valueConverter.convert(val);
				}
				if (ObjectUtil.notNull(val) || includeNullValues) {
					if (ret.length() > 0) {
						ret.append(ObjectUtil.nvlStr(entrySeparator, ","));
					}
					ret.append(key).append(ObjectUtil.nvlStr(keyValueSeparator, ":")).append(val);
				}
			}
			return ret.toString();
		}
		return null;
	}

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Map.class;
	}
	
	//***** PROPERTY ACCESS *****

	public String getKeyValueSeparator() {
		return keyValueSeparator;
	}

	public void setKeyValueSeparator(String keyValueSeparator) {
		this.keyValueSeparator = keyValueSeparator;
	}

	public String getEntrySeparator() {
		return entrySeparator;
	}

	public void setEntrySeparator(String entrySeparator) {
		this.entrySeparator = entrySeparator;
	}

	public DataConverter getKeyConverter() {
		return keyConverter;
	}

	public void setKeyConverter(DataConverter keyConverter) {
		this.keyConverter = keyConverter;
	}

	public DataConverter getValueConverter() {
		return valueConverter;
	}

	public void setValueConverter(DataConverter valueConverter) {
		this.valueConverter = valueConverter;
	}

	public boolean isIncludeNullValues() {
		return includeNullValues;
	}

	public void setIncludeNullValues(boolean includeNullValues) {
		this.includeNullValues = includeNullValues;
	}
}