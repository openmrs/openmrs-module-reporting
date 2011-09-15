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
package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.Patient;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Adapter class for exposing a Person Data Definition as a Patient Data Definition
 */
public class PersonToPatientDataDefinition extends BaseDataDefinition<Patient> implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private PersonDataDefinition definition;
	
	/**
	 * Default Constructor
	 */
	public PersonToPatientDataDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public PersonToPatientDataDefinition(PersonDataDefinition definition) {
		this.definition = definition;
	}
	
	/**
	 * Constructor to populate name only
	 */
	public PersonToPatientDataDefinition(String name, PersonDataDefinition definition) {
		super(name);
		this.definition = definition;
	}

	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return definition.getDataType();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the definition
	 */
	public PersonDataDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(PersonDataDefinition definition) {
		this.definition = definition;
	}
}