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
package org.openmrs.module.reporting.cohort;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.persister.CohortDefinitionPersister;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.util.HandlerUtil;

/**
 * This tests the evaluation of a PatientCharacteristicCohortDefinition
 */
public class StaticCohortDefinitionPersisterTest extends BaseContextSensitiveTest {
	
	/**
	 * @see {@link PatientCharacteristicCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)}
	 */
	@Test
	public void test() throws Exception {
		List<CohortDefinitionPersister> l = 
			HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, StaticCohortDefinition.class);
		Assert.assertEquals(2, l.size());
	}
}
