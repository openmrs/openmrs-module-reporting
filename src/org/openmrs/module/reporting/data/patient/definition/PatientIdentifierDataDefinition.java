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

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Patient Identifier Data Definition
 * TODO: Currently this simply returns the single most preferred identifier value of the given type for each patient
 * We should figure out how to work in limiting by location, showing more than one identifier of a given type,
 * and/or showing compound results (eg. the location and identifier value for each identifier).
 * These might be different definition classes or they might be configuration options on this one...TBD
 */
public class PatientIdentifierDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private PatientIdentifierType type;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public PatientIdentifierDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name and type only
	 */
	public PatientIdentifierDataDefinition(String name, PatientIdentifierType type) {
		super(name);
		this.type = type;
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the type
	 */
	public PatientIdentifierType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PatientIdentifierType type) {
		this.type = type;
	}
}