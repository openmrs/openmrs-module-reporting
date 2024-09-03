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