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

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

/**
 * Adapter class for exposing a Person Data Definition as a Patient Data Definition
 */
public class PersonToPatientDataDefinition extends JoinDataDefinition<PersonDataDefinition> implements PatientDataDefinition {
	
	/**
	 * Default Constructor
	 */
	public PersonToPatientDataDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public PersonToPatientDataDefinition(PersonDataDefinition joinedDefinition) {
		super(joinedDefinition);
	}
	
	/**
	 * Constructor to populate name
	 */
	public PersonToPatientDataDefinition(String name, PersonDataDefinition joinedDefinition) {
		super(name, joinedDefinition);
	}

	/**
	 * @see JoinDataDefinition#getJoinedDefinitionType()
	 */
	@Override
	public Class<PersonDataDefinition> getJoinedDefinitionType() {
		return PersonDataDefinition.class;
	}
}