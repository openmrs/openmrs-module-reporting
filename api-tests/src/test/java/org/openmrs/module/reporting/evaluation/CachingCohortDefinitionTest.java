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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class CachingCohortDefinitionTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
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
		assertNull(ec.getFromCache(maleKey), "Cache should not have male filter yet");

		Cohort maleCohort = Context.getService(CohortDefinitionService.class).evaluate(males, ec);		
		assertNotNull(ec.getFromCache(maleKey), "Cache should have male filter now");
		assertNull(ec.getFromCache(femaleKey), "Cache should not have female filter");

		Cohort malesAgain = Context.getService(CohortDefinitionService.class).evaluate(males, ec);
		assertEquals(maleCohort.size(), malesAgain.size(), "Uncached and cached runs should be equals");
		
		ec.setBaseCohort(maleCohort);
		assertEquals(0, ec.getCache().size(), "Cache should have been automatically cleared");
	}
	
}
