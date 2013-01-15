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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.ScriptedCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ScriptingLanguage;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Tests the ScriptedCohortDefinitionEvaluator
 */
public class ScriptedCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void evaluate_shouldRunScript() throws Exception {
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
		    "org/openmrs/module/reporting/report/script/ScriptedCohortDefinition.txt");
		String script = new String(IOUtils.toByteArray(is), "UTF-8");
		IOUtils.closeQuietly(is);
		
		ScriptedCohortDefinition cohortDefinition = new ScriptedCohortDefinition(new ScriptingLanguage("Groovy"), script);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
		Assert.assertEquals(4, cohort.size());
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(6));
		Assert.assertTrue(cohort.contains(7));
		Assert.assertTrue(cohort.contains(8));
	}
}
