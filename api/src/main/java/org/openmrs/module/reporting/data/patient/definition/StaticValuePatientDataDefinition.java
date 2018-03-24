/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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