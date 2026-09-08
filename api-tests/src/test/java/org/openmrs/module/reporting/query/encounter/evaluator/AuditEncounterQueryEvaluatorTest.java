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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AuditEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.hasExactlyIds;

public class AuditEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    EncounterQueryService encounterQueryService;

    @Autowired
    TestDataManager data;

	Integer e1;
	Integer e2;
	Integer e3;
	Integer e4;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		Patient patient = data.randomPatient().save();
		e1 = data.randomEncounter().patient(patient).encounterType("Scheduled").dateCreated("2013-08-09 10:10:10").save().getId();
		e2 = data.randomEncounter().patient(patient).encounterType("Scheduled").dateCreated("2013-08-10").save().getId();
		e3 = data.randomEncounter().patient(patient).encounterType("Scheduled").dateCreated("2013-08-10 09:09:09").save().getId();
		e4 = data.randomEncounter().patient(patient).encounterType("Emergency").dateCreated("2013-08-11 10:10:10").save().getId();
    }

	@Test
	public void evaluate_shouldLimitByBaseIdSet() throws Exception {
		AuditEncounterQuery query = new AuditEncounterQuery();
		testQueryResults(query, e1, e2, e4);
	}

    @Test
    public void evaluate_shouldFilterByEncounterType() throws Exception {
        AuditEncounterQuery query = new AuditEncounterQuery();
		query.setEncounterTypes(Arrays.asList(Context.getEncounterService().getEncounterType("Scheduled")));
		testQueryResults(query, e1, e2);
		query.setEncounterTypes(Arrays.asList(Context.getEncounterService().getEncounterType("Emergency")));
		testQueryResults(query, e4);
    }

	@Test
	public void evaluate_shouldFilterByMinDateCreated() throws Exception {
		AuditEncounterQuery query = new AuditEncounterQuery();
		query.setCreatedOnOrAfter(DateUtil.getDateTime(2013 ,8, 9));
		testQueryResults(query, e1, e2, e4);
		query.setCreatedOnOrAfter(DateUtil.getDateTime(2013, 8, 9, 10, 10, 10, 0));
		testQueryResults(query, e1, e2, e4);
		query.setCreatedOnOrAfter(DateUtil.getDateTime(2013, 8, 9, 10, 10, 10, 1));
		testQueryResults(query, e2, e4);
	}

	@Test
	public void evaluate_shouldFilterByMaxDateCreated() throws Exception {
		AuditEncounterQuery query = new AuditEncounterQuery();
		query.setCreatedOnOrBefore(DateUtil.getDateTime(2013, 8, 11));
		testQueryResults(query, e1, e2, e4);
		query.setCreatedOnOrBefore(DateUtil.getDateTime(2013, 8, 11, 10, 10, 10, 0));
		testQueryResults(query, e1, e2, e4);
		query.setCreatedOnOrBefore(DateUtil.getDateTime(2013, 8, 11, 10, 10, 9, 0));
		testQueryResults(query, e1, e2);
	}

	@Test
	public void evaluate_shouldFilterByLatestCreatedNumber() throws Exception {
		AuditEncounterQuery query = new AuditEncounterQuery();
		query.setLatestCreatedNumber(10);
		testQueryResults(query, e1, e2, e4);
		query.setLatestCreatedNumber(3);
		testQueryResults(query, e1, e2, e4);
		query.setLatestCreatedNumber(2);
		testQueryResults(query, e2, e4);
	}

	protected void testQueryResults(AuditEncounterQuery query, Integer...expected) throws Exception {
		EncounterEvaluationContext context = new EncounterEvaluationContext();
		context.setBaseEncounters(new EncounterIdSet(e1, e2, e4)); // not 3
		EncounterQueryResult result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(expected));

	}
}
