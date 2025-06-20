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