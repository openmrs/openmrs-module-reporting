/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.definition;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Age Data, calculated for each person based on the Data produced from another Data Definition
 */
@Localized("reporting.AgeAtDateOfOtherDataDefinition")
public class AgeAtDateOfOtherDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	public static final long serialVersionUID = 1L;

	//****** PROPERTIES ******
	
	@ConfigurationProperty(required=false)
	private MappedData<? extends DataDefinition> effectiveDateDefinition; // should be a PersonDataDefinition or PatientDataDefinition
	
	/**
	 * Default Constructor
	 */
	public AgeAtDateOfOtherDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public AgeAtDateOfOtherDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return Age.class;
	}

	/**
	 * @return the effectiveDateDefinition
	 */
	public MappedData<? extends DataDefinition> getEffectiveDateDefinition() {
		return effectiveDateDefinition;
	}

	/**
	 * @param effectiveDateDefinition the effectiveDateDefinition to set
	 */
	public void setEffectiveDateDefinition(MappedData<? extends DataDefinition> effectiveDateDefinition) {
		this.effectiveDateDefinition = effectiveDateDefinition;
	}
}