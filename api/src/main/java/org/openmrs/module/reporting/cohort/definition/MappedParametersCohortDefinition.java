/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.Patient;
import org.openmrs.module.reporting.query.MappedParametersQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows you to easily expose a cohort definition with different names for its parameters (which typically must be the
 * same as @ConfigurationProperty-annotated properties.
 */
public class MappedParametersCohortDefinition extends MappedParametersQuery<CohortDefinition, Patient> implements CohortDefinition {

    public MappedParametersCohortDefinition() { }

	public MappedParametersCohortDefinition(CohortDefinition toWrap, Map<String, String> renamedParameters) {
		super(toWrap, renamedParameters);
	}

	/**
	 * Example usage:
	 * new MappedParametersCohortDefinition(encounterCD, "onOrAfter", "startDate", "onOrBefore", "endDate");
	 * @param toWrap
	 * @param renamedParameters must have an even number of entries. Each pair should be the is the original parameter
	 *                          name, followed by the new parameter name
	 */
	public MappedParametersCohortDefinition(CohortDefinition toWrap, String... renamedParameters) {
		super(toWrap, toMap(renamedParameters));
	}

	private static Map<String, String> toMap(String... keysAndValues) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keysAndValues.length; i += 2) {
			map.put(keysAndValues[i], keysAndValues[i + 1]);
		}
		return map;
	}
}
