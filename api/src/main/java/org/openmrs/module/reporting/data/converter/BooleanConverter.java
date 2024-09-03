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