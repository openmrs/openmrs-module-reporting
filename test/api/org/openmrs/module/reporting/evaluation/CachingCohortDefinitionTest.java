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
package org.openmrs.module.reporting.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 *
 */
public class CachingCohortDefinitionTest extends BaseContextSensitiveTest {
	
	@Test
	public void shouldCacheCohortDefinition() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/reporting/include/CohortDefinitionTest.xml");
		authenticate();
		
		EvaluationContext ec = new EvaluationContext();
		
		GenderCohortDefinition maleFilter = 
			new GenderCohortDefinition();
		maleFilter.setGender("M");
		GenderCohortDefinition femaleFilter = new GenderCohortDefinition();
		femaleFilter.setGender("F");
		ConfigurationPropertyCachingStrategy strategy = new ConfigurationPropertyCachingStrategy();
		String maleKey = strategy.getCacheKey(maleFilter);
		System.out.println(maleKey);
		String femaleKey = strategy.getCacheKey(femaleFilter);
		System.out.println(femaleKey);
		
		assertNull("Cache should not have male filter yet", ec.getFromCache(maleKey));
		
		Cohort males = Context.getService(CohortDefinitionService.class).evaluate(maleFilter, ec);		
		assertNotNull("Cache should have male filter now", ec.getFromCache(maleKey));
		assertNull("Cache should not have female filter", ec.getFromCache(femaleKey));

		Cohort malesAgain = 
			Context.getService(CohortDefinitionService.class).evaluate(maleFilter, ec);
		assertEquals("Uncached and cached runs should be equals", males.size(), malesAgain.size());
		ec.setBaseCohort(males);
		assertEquals("Cache should have been automatically cleared", 0, ec.getCache().size());
	}
	
}
