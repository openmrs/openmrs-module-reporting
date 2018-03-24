/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.ReportingMatchers;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.AllPersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the evaluation of the SqlPersonQuery
 */
public class AllPersonQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	PersonQueryService personQueryService;

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/" + new TestUtil().getTestDatasetFilename("ReportTestDataset"));
	}

	protected void testQuery(EvaluationContext context, Integer...expectedIds) throws EvaluationException {
		PersonQueryResult result = personQueryService.evaluate(new AllPersonQuery(), context);
		if (expectedIds.length == 0) {
			assertThat(result.getSize(), is(0));
		}
		else {
			assertThat(result, ReportingMatchers.hasExactlyIds(expectedIds));
		}
	}

	@Test
	public void testEvaluateWithBaseCohort() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(Arrays.asList(20, 21)));
		testQuery(context, 20, 21);
	}

	@Test
	public void testEvaluateWithBasePersonIds() throws Exception {
		PersonEvaluationContext context = new PersonEvaluationContext();
		context.setBasePersons(new PersonIdSet(2,6,7,8));
		testQuery(context, 2,6,7,8);
	}

	@Test
	public void testEvaluateBothBaseCohortAndBasePersonIds() throws Exception {
		PersonEvaluationContext context = new PersonEvaluationContext();
		context.setBaseCohort(new Cohort(Arrays.asList(20, 21)));
		context.setBasePersons(new PersonIdSet(2,6,7,8));
		testQuery(context); // No overlap, so no results

		context.setBasePersons(new PersonIdSet(20));
		testQuery(context, 20);
	}

	@Test
	public void testEvaluateWithEmptyCohort() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort());
		testQuery(context);
	}

	@Test
	public void testEvaluateWithEmptyBasePersonIds() throws Exception {
		PersonEvaluationContext context = new PersonEvaluationContext();
		context.setBasePersons(new PersonIdSet());
		testQuery(context);
	}

	@Test
	public void testEvaluateWithBasePersonsWhoAreNotPatients() throws Exception {
		PersonEvaluationContext context = new PersonEvaluationContext();
		context.setBasePersons(new PersonIdSet(20,21,501,502));
		testQuery(context, 20, 21, 501, 502);
	}
}