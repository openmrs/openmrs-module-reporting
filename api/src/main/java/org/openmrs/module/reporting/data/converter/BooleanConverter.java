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

/**
 * Boolean data converter
 */
public class BooleanConverter implements DataConverter  {
	
	//***** PROPERTIES *****
	
	private String trueFormat = "true";
	private String falseFormat = "false";
	private String unspecifiedFormat = "";
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default constructor
	 */
	public BooleanConverter() { }
	
	/**
	 * Full constructor
	 */
	public BooleanConverter(String trueFormat, String falseFormat, String unspecifiedFormat) {
		this.trueFormat = trueFormat;
		this.falseFormat = falseFormat;
		this.unspecifiedFormat = unspecifiedFormat;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a Boolean to a configured text representation
	 */
	public Object convert(Object original) {
		Boolean b = (Boolean) original;
		if (b == Boolean.TRUE) {
			return trueFormat;
		}
		else if (b == Boolean.FALSE) {
			return falseFormat;
		}
		return unspecifiedFormat;
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
		return Boolean.class;
	}
	
	//***** PROPERTIES *****

	/**
	 * @return the trueFormat
	 */
	public String getTrueFormat() {
		return trueFormat;
	}

	/**
	 * @param trueFormat the trueFormat to set
	 */
	public void setTrueFormat(String trueFormat) {
		this.trueFormat = trueFormat;
	}

	/**
	 * @return the falseFormat
	 */
	public String getFalseFormat() {
		return falseFormat;
	}

	/**
	 * @param falseFormat the falseFormat to set
	 */
	public void setFalseFormat(String falseFormat) {
		this.falseFormat = falseFormat;
	}

	/**
	 * @return the unspecifiedFormat
	 */
	public String getUnspecifiedFormat() {
		return unspecifiedFormat;
	}

	/**
	 * @param unspecifiedFormat the unspecifiedFormat to set
	 */
	public void setUnspecifiedFormat(String unspecifiedFormat) {
		this.unspecifiedFormat = unspecifiedFormat;
	}
}