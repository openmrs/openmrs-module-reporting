/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EvaluationServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	DbSessionFactory sessionFactory;

	@Autowired
	EvaluationService evaluationService;

	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void evaluateToList_shouldEvaluateAQueryToAMultiValueList() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		List<Object[]> l = evaluationService.evaluateToList(queryBuilder, new EvaluationContext());
		Assertions.assertEquals(2, l.get(0)[0]);
		Assertions.assertEquals(7, l.get(1)[0]);
		Assertions.assertEquals("M", l.get(0)[1]);
		Assertions.assertEquals("F", l.get(1)[1]);
		Assertions.assertEquals(2, l.size());
	}

	@Test
	public void evaluateToList_shouldEvaluateAQueryToASingleValueList() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		List<String> genders = evaluationService.evaluateToList(queryBuilder, String.class, new EvaluationContext());
		Assertions.assertEquals("M", genders.get(0));
		Assertions.assertEquals("F", genders.get(1));
		Assertions.assertEquals(2, genders.size());
	}

    @Test
	public void evaluateToList_shouldThrowAnExceptionWithIncorrectNumberOfColumns() {
		assertThrows(IllegalArgumentException.class, () -> {
			HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
			queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
			evaluationService.evaluateToList(queryBuilder, String.class, new EvaluationContext());
		});
	}

	@Test
	public void evaluateToMap_shouldEvaluateAQueryToAMap() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		Map<Integer, String> m = evaluationService.evaluateToMap(queryBuilder, Integer.class, String.class, new EvaluationContext());
		Assertions.assertEquals(m.get(2), "M");
		Assertions.assertEquals(m.get(7), "F");
	}

    @Test
	public void evaluateToMap_shouldThrowAnExceptionWithIncorrectNumberOfColumns() {
		assertThrows(IllegalArgumentException.class, () -> {
			HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
			queryBuilder.select("personId", "gender", "birthdate").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
			evaluationService.evaluateToMap(queryBuilder, Integer.class, String.class, new EvaluationContext());
		});
	}

	@Test
	public void listResults_shouldNotStackOverflowOnLargeInClauses() throws Exception {
		List<Integer> bigIdSet = new ArrayList<Integer>();
		for (int i=1; i<= 100000; i++) {
			bigIdSet.add(i);
		}
		HqlQueryBuilder hql = new HqlQueryBuilder();
		hql.select("e.encounterDatetime").from(Encounter.class, "e").whereIdIn("e.patient.patientId", bigIdSet);
		Context.getService(EvaluationService.class).evaluateToList(hql, new EvaluationContext());
	}
}