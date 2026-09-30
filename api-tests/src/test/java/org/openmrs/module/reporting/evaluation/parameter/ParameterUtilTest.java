/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.parameter;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests the ParameterUtil methods
 */
public class ParameterUtilTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * Tests that fields annotated as {@link EvalProperty} are added as Parameters
	 */
	@Test
	public void shouldHaveAllAnnotatedFieldsAsParameters() throws Exception {		
		AgeCohortDefinition def = new AgeCohortDefinition();
		List<Property> props = DefinitionUtil.getConfigurationProperties(def);
		Assert.assertEquals(6, props.size());
		for (Property p : props) {
			if (p.getField().getName().equals("minAgeUnit")) {
				Assert.assertEquals(DurationUnit.YEARS, p.getValue());
			}
		}
	}
	
	/**
	 * Tests that fields annotated as {@link EvalProperty} are added as Parameters from superclasses
	 */
	@Test
	public void shouldHaveAllInheritedAnnotatedFieldsAsParameters() throws Exception {		
		NumericObsCohortDefinition def = new NumericObsCohortDefinition();
		// NOTE: This should be changed to 11 when groupingConcept field is implemented
		Assert.assertEquals(10, DefinitionUtil.getConfigurationProperties(def).size());
	}
}
