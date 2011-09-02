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
package org.openmrs.module.reporting.dataset.column.definition.person;

import java.util.Date;

import org.openmrs.module.reporting.dataset.column.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.dataset.column.definition.PropertyColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Age Column
 */
public class AgeColumnDefinition extends BirthdateColumnDefinition {
	
	public static final long serialVersionUID = 1L;

	//****** PROPERTIES ******
	
	@ConfigurationProperty(required=false)
	private Date effectiveDate;
	
	/**
	 * Default Constructor
	 */
	public AgeColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public AgeColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public AgeColumnDefinition(String name, ColumnConverter transform) {
		super(name, transform);
	}
    
	/** 
	 * @see PropertyColumnDefinition#getPropertyConverter()
	 */
	@Override
	public ColumnConverter getPropertyConverter() {
		BirthdateToAgeConverter converter = new BirthdateToAgeConverter();
		converter.setEffectiveDate(getEffectiveDate());
		return converter;
	}

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