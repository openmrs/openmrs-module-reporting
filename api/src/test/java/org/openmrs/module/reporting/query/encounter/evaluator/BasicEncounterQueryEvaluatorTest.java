/*
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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.hasExactlyIds;

public class BasicEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    EncounterQueryService encounterQueryService;

    @Autowired
    TestDataManager data;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        Patient patient = data.randomPatient().save();

        Encounter enc1 = data.randomEncounter().patient(patient).encounterDatetime("2013-08-09 10:10:10").save();
        Encounter enc2 = data.randomEncounter().patient(patient).encounterDatetime("2013-08-10").save();
        Encounter enc3 = data.randomEncounter().patient(patient).encounterDatetime("2013-08-10 09:09:09").save();
        Encounter enc4 = data.randomEncounter().patient(patient).encounterDatetime("2013-08-10 10:10:10").save();
        Encounter enc5 = data.randomEncounter().patient(patient).encounterDatetime("2013-08-11 10:10:10").save();

        BasicEncounterQuery query = new BasicEncounterQuery();
        query.setOnOrAfter(DateUtil.parseYmd("2013-08-10"));
        query.setOnOrBefore(DateUtil.parseYmd("2013-08-10"));

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId(), enc2.getId(), enc4.getId(), enc5.getId())); // not 3
        EncounterQueryResult result = encounterQueryService.evaluate(query, context);
        assertThat(result, hasExactlyIds(enc2.getId(), enc4.getId())); // 3 is excluded, since it wasn't in base encounters
    }

	@Test
	public void testShouldFilterByEncounterTypes() throws Exception {
		Patient patient = data.randomPatient().save();

		Encounter enc1 = data.randomEncounter().patient(patient).encounterType("Scheduled").save();
		Encounter enc2 = data.randomEncounter().patient(patient).encounterType("Emergency").save();
		Encounter enc3 = data.randomEncounter().patient(patient).encounterType("Emergency").save();

		EncounterEvaluationContext context = new EncounterEvaluationContext();
		context.setBaseEncounters(new EncounterIdSet(enc1.getId(), enc2.getId(), enc3.getId()));

		BasicEncounterQuery query = new BasicEncounterQuery();

		query.addEncounterType(Context.getEncounterService().getEncounterType("Scheduled"));
		EncounterQueryResult result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(enc1.getId()));

		query.addEncounterType(Context.getEncounterService().getEncounterType("Emergency"));
		result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(enc1.getId(), enc2.getId(), enc3.getId()));
	}

	@Test
	public void testShouldFilterByForms() throws Exception {

		EncounterEvaluationContext context = new EncounterEvaluationContext();
		context.setBaseEncounters(new EncounterIdSet(3,4,5,6,7,8,9));
		BasicEncounterQuery query = new BasicEncounterQuery();

		query.addForm(Context.getFormService().getForm("Intake Form"));
		EncounterQueryResult result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(3,4,5));

		query.addForm(Context.getFormService().getForm("Basic Form"));
		result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(3,4,5,6,7,8,9));
	}

	@Test
	public void testShouldFilterByLocations() throws Exception {
		Patient patient = data.randomPatient().save();

		Encounter enc1 = data.randomEncounter().patient(patient).location("Xanadu").save();
		Encounter enc2 = data.randomEncounter().patient(patient).location("Never Never Land").save();
		Encounter enc3 = data.randomEncounter().patient(patient).location("Never Never Land").save();

		EncounterEvaluationContext context = new EncounterEvaluationContext();
		context.setBaseEncounters(new EncounterIdSet(enc1.getId(), enc2.getId(), enc3.getId()));

		BasicEncounterQuery query = new BasicEncounterQuery();

		query.addLocation(Context.getLocationService().getLocation("Xanadu"));
		EncounterQueryResult result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(enc1.getId()));

		query.addLocation(Context.getLocationService().getLocation("Never Never Land"));
		result = encounterQueryService.evaluate(query, context);
		assertThat(result, hasExactlyIds(enc1.getId(), enc2.getId(), enc3.getId()));
	}

	@Test
	public void testShouldFilterByWhich() throws Exception {
		Patient patient1 = data.randomPatient().save();
		Encounter enc1 = data.randomEncounter().encounterDatetime("2014-01-01").patient(patient1).save();
		Encounter enc2 = data.randomEncounter().encounterDatetime("2014-02-01").patient(patient1).save();
		Encounter enc3 = data.randomEncounter().encounterDatetime("2014-03-01").patient(patient1).save();
		Patient patient2 = data.randomPatient().save();
		Encounter enc4 = data.randomEncounter().encounterDatetime("2014-04-01").patient(patient2).save();

		EncounterEvaluationContext context = new EncounterEvaluationContext();
		context.setBaseEncounters(new EncounterIdSet(enc1.getId(), enc2.getId(), enc3.getId(), enc4.getId()));

		{
			BasicEncounterQuery query = new BasicEncounterQuery();
			query.setWhich(TimeQualifier.LAST);
			query.setWhichNumber(2);
			EncounterQueryResult result = encounterQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(enc2.getId(), enc3.getId(), enc4.getId()));
		}
		{
			BasicEncounterQuery query = new BasicEncounterQuery();
			query.setWhich(TimeQualifier.FIRST);
			query.setWhichNumber(1);
			EncounterQueryResult result = encounterQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(enc1.getId(), enc4.getId()));
		}
		{
			BasicEncounterQuery query = new BasicEncounterQuery();
			query.setWhich(TimeQualifier.FIRST);
			EncounterQueryResult result = encounterQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(enc1.getId(), enc4.getId()));
		}
		{
			BasicEncounterQuery query = new BasicEncounterQuery();
			EncounterQueryResult result = encounterQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(enc1.getId(), enc2.getId(), enc3.getId(), enc4.getId()));
		}
	}
}
