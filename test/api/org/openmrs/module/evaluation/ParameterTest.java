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
package org.openmrs.module.evaluation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.evaluation.parameter.Parameter;

/**
 * Contains a variety of Parameter-related tests
 */
public class ParameterTest {
	
	/**
	 * Tests that fields annotated as {@link Parameterized} are added as Parameters to a 
	 * Parameterizable instance
	 */
	@Test
	public void shouldHaveAllParameterizedFieldsAsParameters() throws Exception {		
		CompoundCohortDefinition def = new CompoundCohortDefinition();
		System.out.println(def.getAvailableParameters());
		Assert.assertEquals(2, def.getAvailableParameters().size());
		for (Parameter p : def.getAvailableParameters()) {
			if (p.getName().equals("operator")) {
				Assert.assertEquals(BooleanOperator.OR, p.getDefaultValue());
			}
			else if (p.getName().equals("definitions")) {
				Assert.assertTrue(p.isRequired());
			}
		}
	}
}
