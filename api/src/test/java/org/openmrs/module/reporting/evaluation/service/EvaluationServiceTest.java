package org.openmrs.module.reporting.evaluation.service;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluationServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	EvaluationService evaluationService;

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void evaluateToList_shouldEvaluateAQueryToAMultiValueList() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		List<Object[]> l = evaluationService.evaluateToList(queryBuilder, new EvaluationContext());
		Assert.assertEquals(2, l.get(0)[0]);
		Assert.assertEquals(7, l.get(1)[0]);
		Assert.assertEquals("M", l.get(0)[1]);
		Assert.assertEquals("F", l.get(1)[1]);
		Assert.assertEquals(2, l.size());
	}

	@Test
	public void evaluateToList_shouldEvaluateAQueryToASingleValueList() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		List<String> genders = evaluationService.evaluateToList(queryBuilder, String.class, new EvaluationContext());
		Assert.assertEquals("M", genders.get(0));
		Assert.assertEquals("F", genders.get(1));
		Assert.assertEquals(2, genders.size());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void evaluateToList_shouldThrowAnExceptionWithIncorrectNumberOfColumns() {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		evaluationService.evaluateToList(queryBuilder, String.class, new EvaluationContext());
	}

	@Test
	public void evaluateToMap_shouldEvaluateAQueryToAMap() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		Map<Integer, String> m = evaluationService.evaluateToMap(queryBuilder, Integer.class, String.class, new EvaluationContext());
		Assert.assertEquals(m.get(2), "M");
		Assert.assertEquals(m.get(7), "F");
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void evaluateToMap_shouldThrowAnExceptionWithIncorrectNumberOfColumns() {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender", "birthdate").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		evaluationService.evaluateToMap(queryBuilder, Integer.class, String.class, new EvaluationContext());
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