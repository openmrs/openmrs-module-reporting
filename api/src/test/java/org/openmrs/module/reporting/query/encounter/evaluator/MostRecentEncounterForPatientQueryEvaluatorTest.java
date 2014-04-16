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
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MostRecentEncounterForPatientQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class MostRecentEncounterForPatientQueryEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	TestDataManager tdm;

	@Autowired
	EncounterQueryService encounterQueryService;
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link MostRecentEncounterForPatientQueryEvaluator#evaluate(EncounterQuery,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find an encounter on the onOrBefore date if passed in time is at midnight", method = "evaluate(EncounterQuery,EvaluationContext)")
	public void evaluate_shouldFindAnEncounterOnTheOnOrBeforeDateIfPassedInTimeIsAtMidnight() throws Exception {

		Date date = new Date();
		Encounter enc = tdm.encounter().location(1).encounterType(1).encounterDatetime(date).patient(7).save();
		
		MostRecentEncounterForPatientQuery query = new MostRecentEncounterForPatientQuery();
		query.setOnOrBefore(DateUtil.getStartOfDay(date));
		Cohort cohort = new Cohort("7");
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(cohort);
		EncounterQueryResult result = encounterQueryService.evaluate(query, context);
		Assert.assertEquals(enc.getEncounterId(), result.getMemberIds().iterator().next());
	}
	
}
