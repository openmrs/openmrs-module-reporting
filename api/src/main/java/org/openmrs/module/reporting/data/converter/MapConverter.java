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