/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.visit.evaluator;

import static org.openmrs.module.reporting.common.ReportingMatchers.hasExactlyIds;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.BasicVisitQuery;
import org.openmrs.module.reporting.query.visit.service.VisitQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class BasicVisitQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	private VisitQueryService visitQueryService;

	@Autowired
	private VisitService visitService;

	@Autowired
	TestDataManager data;

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void testEvaluate() throws Exception {

		// there are some active visits in the dataset already
		List<Integer> activeVisits = new ArrayList<Integer>();
		activeVisits.add(1);
		activeVisits.add(2);
		activeVisits.add(3);
		activeVisits.add(4);
		activeVisits.add(5);

		// now we will create a couple inactive visits, and two active ones
		Patient patient1 = data.randomPatient().birthdate("1975-05-27").save();
		Patient patient2 = data.randomPatient().birthdate("1975-05-27").save();
		Visit inactive1 = data.visit().patient(patient1).visitType(1).location(1).started("2013-04-01").stopped("2013-04-06").save();
		Visit inactive2 = data.visit().patient(patient2).visitType(1).location(1).started("2013-04-01").stopped("2013-04-15").save();
		activeVisits.add(inactive1.getId());
		activeVisits.add(inactive2.getId());

		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.setStartedOnOrBefore(DateUtil.adjustDate(visitService.getVisit(activeVisits.get(0)).getStartDatetime(), -1, DurationUnit.DAYS));
			VisitQueryResult result = visitQueryService.evaluate(query, new VisitEvaluationContext());
			assertEquals(result.getSize(), 0);
		}

		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.setStartedOnOrAfter(visitService.getVisit(activeVisits.get(0)).getStartDatetime());
			query.setStartedOnOrBefore(DateUtil.adjustDate(visitService.getVisit(activeVisits.get(0)).getStartDatetime(), +1, DurationUnit.DAYS));
			VisitQueryResult result = visitQueryService.evaluate(query, new VisitEvaluationContext());
			assertEquals(result.getSize(), 5);
		}

		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.setStartedOnOrAfter(inactive1.getStartDatetime());
			query.setStartedOnOrBefore(DateUtil.adjustDate(inactive1.getStartDatetime(), +1, DurationUnit.DAYS));
			query.setEndedOnOrAfter(inactive1.getStopDatetime());
			VisitQueryResult result = visitQueryService.evaluate(query, new VisitEvaluationContext());
			assertEquals(result.getSize(), 2);
		}

		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.setStartedOnOrAfter(inactive1.getStartDatetime());
			query.setStartedOnOrBefore(DateUtil.adjustDate(inactive1.getStartDatetime(), +1, DurationUnit.DAYS));
			query.setEndedOnOrAfter(inactive1.getStopDatetime());
			query.setEndedOnOrBefore(DateUtil.adjustDate(inactive1.getStopDatetime(), +1, DurationUnit.DAYS));
			VisitQueryResult result = visitQueryService.evaluate(query, new VisitEvaluationContext());
			assertEquals(result.getSize(), 1);
		}
	}

	@Test
	public void testShouldFilterByVisitTypes() throws Exception {

		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseVisits(new VisitIdSet(1,2,3,4,5));

		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.addVisitType(Context.getVisitService().getVisitType(1));
			VisitQueryResult result = visitQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(1,2,4,5));
		}
		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.addVisitType(Context.getVisitService().getVisitType(2));
			VisitQueryResult result = visitQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(3));
		}
	}

	@Test
	public void testShouldFilterByLocations() throws Exception {

		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseVisits(new VisitIdSet(1,2,3,4,5));

		{
			BasicVisitQuery query = new BasicVisitQuery();
			query.addLocation(Context.getLocationService().getLocation(1));
			VisitQueryResult result = visitQueryService.evaluate(query, context);
			assertThat(result, hasExactlyIds(1));
		}
	}
}
