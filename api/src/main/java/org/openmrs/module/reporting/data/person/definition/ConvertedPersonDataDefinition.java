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
