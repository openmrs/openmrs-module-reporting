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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.cohort.definition.configuration.Property;
import org.openmrs.module.cohort.definition.util.CohortDefinitionUtil;

/**
 * Tests the ParameterUtil methods
 */
public class ParameterUtilTest {
	
	/**
	 * Tests that fields annotated as {@link EvalProperty} are added as Parameters
	 */
	@Test
	public void shouldHaveAllAnnotatedFieldsAsParameters() throws Exception {		
		CompoundCohortDefinition def = new CompoundCohortDefinition();
		List<Property> props = CohortDefinitionUtil.getConfigurationProperties(def);
		Assert.assertEquals(2, props.size());
		for (Property p : props) {
			if (p.getField().getName().equals("operator")) {
				Assert.assertEquals(BooleanOperator.AND, p.getValue());
			}
			else if (p.getField().getName().equals("definitions")) {
				Assert.assertTrue(p.getRequired());
			}
		}
	}
	
	/**
	 * Tests that fields annotated as {@link EvalProperty} are added as Parameters from superclasses
	 */
	@Test
	public void shouldHaveAllInheritedAnnotatedFieldsAsParameters() throws Exception {		
		DrugOrderCohortDefinition def = new DrugOrderCohortDefinition();
		System.out.println(CohortDefinitionUtil.getConfigurationProperties(def));
		Assert.assertEquals(9, CohortDefinitionUtil.getConfigurationProperties(def).size());
	}
}
