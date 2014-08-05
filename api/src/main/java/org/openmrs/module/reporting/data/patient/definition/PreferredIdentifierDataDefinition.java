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

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Patient Identifier Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PreferredIdentifierDataDefinition")
public class PreferredIdentifierDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=false)
	private PatientIdentifierType identifierType;

	@ConfigurationProperty(required=false)
	private Location location;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public PreferredIdentifierDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name and type only
	 */
	public PreferredIdentifierDataDefinition(String name, PatientIdentifierType identifierType) {
		super(name);
		this.identifierType = identifierType;
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return PatientIdentifier.class;
	}
	
	//****** PROPERTY ACCESS ******

	public PatientIdentifierType getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(PatientIdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}