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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Data Definition that simply returns the value it is configured with for all patients
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.StaticValuePatientDataDefinition")
public class StaticValuePatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty
	private Object staticValue;

	/**
	 * Default Constructor
	 */
	public StaticValuePatientDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public StaticValuePatientDataDefinition(Object staticValue) {
		this.staticValue = staticValue;
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return Object.class;
	}

	public Object getStaticValue() {
		return staticValue;
	}

	public void setStaticValue(Object staticValue) {
		this.staticValue = staticValue;
	}
}