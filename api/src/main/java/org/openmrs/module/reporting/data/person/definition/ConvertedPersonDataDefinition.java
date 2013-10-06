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
package org.openmrs.module.reporting.data.person.definition;

import org.openmrs.module.reporting.data.ConvertedDataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

/**
 * Implementation of Converted Data Definition for Patient Data
 */
public class ConvertedPersonDataDefinition extends ConvertedDataDefinition<PersonDataDefinition> implements PersonDataDefinition {

	/**
	 * Default Constructor
	 */
	public ConvertedPersonDataDefinition() {
		super();
	}

	/**
	 * Default Constructor
	 */
	public ConvertedPersonDataDefinition(String name, PersonDataDefinition definitionToConvert, DataConverter... converters) {
		super(name, definitionToConvert, converters);
	}
}
