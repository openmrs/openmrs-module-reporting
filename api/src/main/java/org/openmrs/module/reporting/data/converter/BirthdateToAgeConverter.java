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

import java.util.Date;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Converters a Birthdate into an Age
 */
public class BirthdateToAgeConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private Date effectiveDate;
	
	//***** CONSTRUCTORS *****
	
	public BirthdateToAgeConverter() {}
	
	/**
	 * Full Constructor
	 */
	public BirthdateToAgeConverter(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a birthdate to an age on the configured date
	 */
	public Object convert(Object original) {
		Birthdate birthdate = (Birthdate) original;
		if (birthdate != null) {
			return new Age(birthdate.getBirthdate(), effectiveDate);
		}
		return null;
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return Age.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Birthdate.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the effectiveDate
	 */
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * @param effectiveDate the effectiveDate to set
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}