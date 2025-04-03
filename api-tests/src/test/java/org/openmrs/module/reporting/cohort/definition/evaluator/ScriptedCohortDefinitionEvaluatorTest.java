/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
