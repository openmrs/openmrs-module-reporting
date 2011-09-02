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
package org.openmrs.module.reporting.dataset.column.definition.patient;

import org.openmrs.OpenmrsData;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.column.definition.LinkedPropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.PropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;

/**
 * Person Attribute Column
 */
public class PatientIdentifierColumnDefinition extends LinkedPropertyColumnDefinition<PatientIdentifierType> implements PatientColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public PatientIdentifierColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public PatientIdentifierColumnDefinition(PatientIdentifierType type, String name) {
		super(type, name);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public PatientIdentifierColumnDefinition(PatientIdentifierType type, String name, ColumnConverter transform) {
		super(type, name, transform);
	}
	
	//****** INSTANCE METHODS ******

	/** 
	 * @see RowPerObjectColumnDefinition#getBaseType()
	 */
	public Class<? extends OpenmrsData> getBaseType() {
		return PatientIdentifier.class;
	}
	
	/** 
	 * @see RowPerObjectColumnDefinition#getIdProperty()
	 */
	public String getIdProperty() {
		return "patientId";
	}

	/** 
	 * @see PropertyColumnDefinition#getProperty()
	 */
	@Override
	public String getProperty() {
		return "identifier";
	}
}