/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class CachingCohortDefinitionTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void shouldCacheCohortDefinition() throws Exception {
		
		EvaluationContext ec = new EvaluationContext();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setMaleIncluded(true);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setFemaleIncluded(true);
		
		ConfigurationPropertyCachingStrategy strategy = new ConfigurationPropertyCachingStrategy();
		String maleKey = strategy.getCacheKey(males, ec);
		String femaleKey = strategy.getCacheKey(females, ec);
		assertNull("Cache should not have male filter yet", ec.getFromCache(maleKey));

		Cohort maleCohort = Context.getService(CohortDefinitionService.class).evaluate(males, ec);		
		assertNotNull("Cache should have male filter now", ec.getFromCache(maleKey));
		assertNull("Cache should not have female filter", ec.getFromCache(femaleKey));

		Cohort malesAgain = Context.getService(CohortDefinitionService.class).evaluate(males, ec);
		assertEquals("Uncached and cached runs should be equals", maleCohort.size(), malesAgain.size());
		
		ec.setBaseCohort(maleCohort);
		assertEquals("Cache should have been automatically cleared", 0, ec.getCache().size());
	}
	
}
