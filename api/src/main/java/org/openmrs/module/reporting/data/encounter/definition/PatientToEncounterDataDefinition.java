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

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

/**
 * Adapter class for exposing a Patient Data Definition as an Encounter Data Definition
 */
public class PatientToEncounterDataDefinition extends JoinDataDefinition<PatientDataDefinition> implements EncounterDataDefinition {
	
	/**
	 * Default Constructor
	 */
	public PatientToEncounterDataDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public PatientToEncounterDataDefinition(PatientDataDefinition joinedDataDefinition) {
		super(joinedDataDefinition);
	}
	
	/**
	 * Constructor to populate name
	 */
	public PatientToEncounterDataDefinition(String name, PatientDataDefinition joinedDataDefinition) {
		super(name, joinedDataDefinition);
	}

	/**
	 * @see JoinDataDefinition#getJoinedDefinitionType()
	 */
	@Override
	public Class<PatientDataDefinition> getJoinedDefinitionType() {
		return PatientDataDefinition.class;
	}
}