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
package org.openmrs.module.evaluation.parameter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.cohort.definition.DrugOrderCohortDefinition;

/**
 * Tests the ParameterUtil methods
 */
public class ParameterUtilTest {
	
	/**
	 * Tests that fields annotated as {@link Param} are added as Parameters
	 */
	@Test
	public void shouldHaveAllAnnotatedFieldsAsParameters() throws Exception {		
		CompoundCohortDefinition def = new CompoundCohortDefinition();
		Assert.assertEquals(2, def.getAvailableParameters().size());
		for (Parameter p : def.getAvailableParameters()) {
			if (p.getName().equals("operator")) {
				Assert.assertEquals(BooleanOperator.AND, p.getDefaultValue());
			}
			else if (p.getName().equals("definitions")) {
				Assert.assertTrue(p.isRequired());
			}
		}
	}
	
	/**
	 * Tests that fields annotated as {@link Param} are added as Parameters from superclasses
	 */
	@Test
	public void shouldHaveAllInheritedAnnotatedFieldsAsParameters() throws Exception {		
		DrugOrderCohortDefinition def = new DrugOrderCohortDefinition();
		System.out.println(def.getAvailableParameters());
		Assert.assertEquals(9, def.getAvailableParameters().size());
	}
}
