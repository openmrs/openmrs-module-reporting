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
package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Adapter class for exposing a Patient Data Definition as an Encounter Data Definition
 */
public class PatientToEncounterDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private PatientDataDefinition definition;
	
	/**
	 * Default Constructor
	 */
	public PatientToEncounterDataDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public PatientToEncounterDataDefinition(PatientDataDefinition definition) {
		this.definition = definition;
	}
	
	/**
	 * Constructor to populate name only
	 */
	public PatientToEncounterDataDefinition(String name, PatientDataDefinition definition) {
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
	public PatientDataDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(PatientDataDefinition definition) {
		this.definition = definition;
	}
}