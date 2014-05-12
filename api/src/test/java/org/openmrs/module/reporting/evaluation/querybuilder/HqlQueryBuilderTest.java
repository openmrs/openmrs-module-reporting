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
package org.openmrs.module.reporting.evaluation.querybuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Tests for the EvaluationContext expression parsing
 */
public class HqlQueryBuilderTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	EvaluationService evaluationService;

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void select_shouldSelectTheConfiguredColumns() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		List<Object[]> results = evaluationService.evaluateToList(q);
		testSize(results, 2);
		testRow(results, 1, 2, "M");
		testRow(results, 2, 7, "F");
	}

	@Test
	public void from_shouldNotRequireAnAlias() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personId", "gender").from(Person.class);
		evaluationService.evaluateToList(q);
	}

	@Test
	public void from_shouldAllowAnAlias() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personId", "p.gender").from(Person.class, "p").whereInAny("p.personId", 2, 7);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void from_shouldExcludedVoidedByDefault() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("patientIdentifierId").from(PatientIdentifier.class);
		testSize(evaluationService.evaluateToList(q), 12);
	}

	@Test
	public void from_shouldSupportIncludingVoided() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder(true);
		q.select("patientIdentifierId").from(PatientIdentifier.class);
		testSize(evaluationService.evaluateToList(q), 13);
	}

	@Test
	public void from_shouldDoAnImplicitInnerJoin() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		// This does an implicit inner join apparently, ugh
		q.select("p.personAddressId", "p.changedBy").from(PersonAddress.class, "p");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 1);
	}

	@Test
	public void leftOuterJoin_shouldJoin() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personAddressId", "c").from(PersonAddress.class, "p").leftOuterJoin("p.changedBy", "c");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 7);
	}

	@Test
	public void innerJoin_shouldJoin() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personAddressId", "c").from(PersonAddress.class, "p").innerJoin("p.changedBy", "c");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 1);
	}

	@Test
	public void where_shouldSupportAnArbitraryConstraint() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personId", "p.gender").from(Person.class, "p").where("gender = 'F'").whereInAny("personId", 2, 7);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 1);
	}

	@Test
	public void whereNull_shouldConstrainAgainstNullValues() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder(true);
		q.select("personId").from(Person.class).whereNull("birthdate");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 5);
	}

	@Test
	public void whereEqual_shouldNotConstrainIfValuesAreNull() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personId").from(Person.class).whereEqual("gender", null).whereInAny("personId", 2, 7);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereEqual_shouldConstrainAgainstExactDatetimeIfNotMidnight() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("person.personId").from(PersonAddress.class).whereEqual("dateCreated", DateUtil.getDateTime(2008,8,15,15,46,47,0));
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 1);
	}

	@Test
	public void whereEqual_shouldConstrainAgainstAnyTimeDuringDateIfMidnight() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("person.personId").from(PersonAddress.class).whereEqual("dateCreated", DateUtil.getDateTime(2008,8,15));
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereEqual_shouldConstrainAgainstASimpleValue() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("person.personId").from(PersonAddress.class).whereEqual("cityVillage", "Gucha");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 1);
	}

	@Test
	public void whereEqual_shouldConstrainAgainstAnOpenmrsObject() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		Person person2 = Context.getPersonService().getPerson(2);
		q.select("person.gender").from(PersonAddress.class).whereEqual("person", person2);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 1);
		testRow(rows, 1, "M");
	}

	@Test
	public void whereEqual_shouldConstrainByCohort() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		Cohort c = new Cohort("2,7");
		q.select("person.gender").from(PersonAddress.class).whereEqual("person.personId", c);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereEqual_shouldConstrainByIdSet() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		PatientIdSet idSet = new PatientIdSet(2,7);
		q.select("person.gender").from(PersonAddress.class).whereEqual("person.personId", idSet);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereEqual_shouldConstrainByCollection() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		Set<Integer> idSet = new HashSet<Integer>(Arrays.asList(2,7));
		q.select("person.gender").from(PersonAddress.class).whereEqual("person.personId", idSet);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereEqual_shouldConstrainByArray() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		Integer[] idSet = {2,7};
		q.select("person.gender").from(PersonAddress.class).whereEqual("person.personId", idSet);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereIn_shouldConstrainByCollection() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		Set<Integer> idSet = new HashSet<Integer>(Arrays.asList(2,7));
		q.select("person.gender").from(PersonAddress.class).whereIn("person.personId", idSet);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereIn_shouldConstrainByArray() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		Integer[] idSet = {2,7};
		q.select("person.gender").from(PersonAddress.class).whereInAny("person.personId", idSet);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereLike_shouldConstrainByLike() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("patient.patientId").from(PatientIdentifier.class).whereLike("identifier", "101%");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereGreater_shouldConstrainColumnsGreaterThanValue() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder(true);
		q.select("personId").from(Person.class).whereGreater("personId", 501);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 2);
	}

	@Test
	public void whereGreaterOrEqualTo_shouldConstrainColumnsGreaterOrEqualToValue() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder(true);
		q.select("personId").from(Person.class).whereGreaterOrEqualTo("personId", 501);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 3);
	}

	@Test
	public void whereLess_shouldConstrainColumnsLessThanValue() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder(true);
		q.select("personId").from(Person.class).whereLess("personId", 9);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 5);
	}

	@Test
	public void whereLess_shouldConstrainDateByEndOfDayIfMidnightPassedIn() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personAddressId").from(PersonAddress.class).whereLess("dateCreated", DateUtil.getDateTime(2008,8,15));
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 6);
	}

	@Test
	public void whereLess_shouldConstrainDateByExactTimeIfNotMidnight() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personAddressId").from(PersonAddress.class).whereLess("dateCreated", DateUtil.getDateTime(2008,8,15,15,46,0,0));
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 4);
	}

	@Test
	public void whereLessOrEqualTo_shouldConstrainColumnsLessOrEqualToValue() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder(true);
		q.select("personId").from(Person.class).whereLessOrEqualTo("personId", 9);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 6);
	}

	@Test
	public void whereLessOrEqualTo_shouldConstrainDateByEndOfDayIfMidnightPassedIn() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personAddressId").from(PersonAddress.class).whereLessOrEqualTo("dateCreated", DateUtil.getDateTime(2008,8,15));
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 6);
	}

	@Test
	public void whereLessOrEqualTo_shouldConstrainDateByExactTimeIfNotMidnight() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personAddressId").from(PersonAddress.class).whereLessOrEqualTo("dateCreated", DateUtil.getDateTime(2008,8,15,15,46,47,0));
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 5);
	}

	@Test
	public void whereBetweenInclusive_shouldConstrainBetweenValues() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personId").from(Person.class).whereBetweenInclusive("personId", 20, 30);
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testSize(rows, 5);
	}

	@Test
	public void orderAsc_shouldOrderAscending() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personId").from(Person.class).whereBetweenInclusive("personId", 20, 22).orderAsc("personId");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testRow(rows, 1, 20);
		testRow(rows, 2, 21);
		testRow(rows, 3, 22);
	}

	@Test
	public void orderDesc_shouldOrderDescending() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("personId").from(Person.class).whereBetweenInclusive("personId", 20, 22).orderDesc("personId");
		List<Object[]> rows = evaluationService.evaluateToList(q);
		testRow(rows, 1, 22);
		testRow(rows, 2, 21);
		testRow(rows, 3, 20);
	}

    @Test
    public void whereVisitId_shouldConstrainByVisit() throws Exception {
        HqlQueryBuilder q = new HqlQueryBuilder();
        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet(Arrays.asList(2,3,4)));
        q.select("visitId").from(Visit.class).whereVisitIn("visitId", context);
        List<Object[]> rows = evaluationService.evaluateToList(q);
        testSize(rows, 3);
        testRow(rows, 1, 2);
        testRow(rows, 2, 3);
        testRow(rows, 3, 4);
    }

	// Utility methods

	protected void testSize(List<Object[]> results, int size) {
		Assert.assertEquals(size, results.size());
	}

	protected void testRow(List<Object[]> results, int rowNum, Object... expected) {
		Object[] row = results.get(rowNum-1);
		Assert.assertEquals(expected.length, row.length);
		for (int i=0; i<expected.length; i++) {
			Assert.assertEquals(expected[i], row[i]);
		}
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		//p.setProperty("hibernate.show_sql", "true");
		return p;
	}

	@Test
	@Ignore
	public void testHqlGeneration() throws Exception {
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pa.person.personId", "pa").from(PersonAddress.class, "pa");
		System.out.println("******************* Running test query");
		List<Object[]> results = evaluationService.evaluateToList(q);
		for (Object[] row : results) {
			System.out.println(ObjectUtil.toString(",", row));
		}
	}
}
