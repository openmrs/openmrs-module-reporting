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

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * String data converter
 */
public class StringConverter implements DataConverter  {
	
	//***** PROPERTIES *****
	
	private Map<Object, String> conversions;
	private String unspecifiedValue;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default constructor
	 */
	public StringConverter() { }
	
	/**
	 * Full constructor
	 */
	public StringConverter(Map<Object, String> conversions, String unspecifiedValue) {
		this.conversions = conversions;
		this.unspecifiedValue = unspecifiedValue;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert an Object to a configured String representation
	 */
	public Object convert(Object original) {
		String ret = getConversions().get(original);
		return ObjectUtil.nvl(ret, unspecifiedValue);
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
	
	//***** PROPERTIES *****

	/**
	 * @return the conversions
	 */
	public Map<Object, String> getConversions() {
		if (conversions == null) {
			conversions = new HashMap<Object, String>();
		}
		return conversions;
	}

	/**
	 * @param conversions the conversions to set
	 */
	public void setConversions(Map<Object, String> conversions) {
		this.conversions = conversions;
	}
	
	/**
	 * Adds a conversion for the given value
	 * @param value
	 * @param conversion
	 */
	public void addConversion(Object value, String conversion) {
		getConversions().put(value, conversion);
	}

	/**
	 * @return the unspecifiedValue
	 */
	public String getUnspecifiedValue() {
		return unspecifiedValue;
	}

	/**
	 * @param unspecifiedValue the unspecifiedValue to set
	 */
	public void setUnspecifiedValue(String unspecifiedValue) {
		this.unspecifiedValue = unspecifiedValue;
	}
}