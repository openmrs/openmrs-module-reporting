/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
