package org.openmrs.module.reporting.evaluation.service;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		evaluationService.resetAllIdSets();
	}

	@Test
	public void evaluateToList_shouldEvaluateAQueryToAMultiValueList() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		List<Object[]> l = evaluationService.evaluateToList(queryBuilder);
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
		List<String> genders = evaluationService.evaluateToList(queryBuilder, String.class);
		Assert.assertEquals("M", genders.get(0));
		Assert.assertEquals("F", genders.get(1));
		Assert.assertEquals(2, genders.size());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void evaluateToList_shouldThrowAnExceptionWithIncorrectNumberOfColumns() {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		evaluationService.evaluateToList(queryBuilder, String.class);
	}

	@Test
	public void evaluateToMap_shouldEvaluateAQueryToAMap() throws Exception {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		Map<Integer, String> m = evaluationService.evaluateToMap(queryBuilder, Integer.class, String.class);
		Assert.assertEquals(m.get(2), "M");
		Assert.assertEquals(m.get(7), "F");
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void evaluateToMap_shouldThrowAnExceptionWithIncorrectNumberOfColumns() {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("personId", "gender", "birthdate").from(Person.class).whereInAny("personId", 2, 7).orderAsc("personId");
		evaluationService.evaluateToMap(queryBuilder, Integer.class, String.class);
	}

	@Test
	public void generateKey_shouldGenerateUniqueKeysForEachIdSet() throws Exception {
		Set<Integer> idSet1 = new HashSet<Integer>(Arrays.asList(10,20));
		Set<Integer> idSet2 = new HashSet<Integer>(Arrays.asList(5,10,15));
		Set<Integer> idSet3 = new HashSet<Integer>(Arrays.asList(20,10));

		Assert.assertEquals(evaluationService.generateKey(idSet1), evaluationService.generateKey(idSet1));
		Assert.assertNotEquals(evaluationService.generateKey(idSet1), evaluationService.generateKey(idSet2));
		Assert.assertEquals(evaluationService.generateKey(idSet1), evaluationService.generateKey(idSet3));
	}

	@Test
	public void startUsing_shouldPersistIdSetToTemporaryTableWhenYouStartUsingIt() throws Exception {
		Set<Integer> idSet1 = new HashSet<Integer>(Arrays.asList(2,7));
		String key = evaluationService.generateKey(idSet1);
		checkIdSetMembers(key);
		evaluationService.startUsing(idSet1);
		checkIdSetMembers(key, 2, 7);
		evaluationService.stopUsing(key);
		checkIdSetMembers(key);
	}

	@Test
	public void startUsing_shouldPersistAllIdSetsInEvaluationContext() throws Exception {
		Set<Integer> pIds = new HashSet<Integer>(Arrays.asList(2,7));
		String pIdKey = evaluationService.generateKey(pIds);

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort(pIds));

		checkIdSetMembers(pIdKey);
		evaluationService.startUsing(context);
		checkIdSetMembers(pIdKey, 2, 7);
		evaluationService.stopUsing(pIdKey);
		checkIdSetMembers(pIdKey);

		Set<Integer> encIds = new HashSet<Integer>(Arrays.asList(100,200));
		String encIdKey = evaluationService.generateKey(encIds);

		EncounterEvaluationContext eec = new EncounterEvaluationContext();
		eec.setBaseCohort(new Cohort(pIds));
		eec.setBaseEncounters(new EncounterIdSet(encIds));
		checkIdSetMembers(pIdKey);
		checkIdSetMembers(encIdKey);
		evaluationService.startUsing(eec);
		checkIdSetMembers(pIdKey, 2, 7);
		checkIdSetMembers(encIdKey, 100, 200);
		evaluationService.stopUsing(eec);
		checkIdSetMembers(pIdKey);
		checkIdSetMembers(encIdKey);
	}

	@Test
	public void startUsing_shouldNotPersistIdSetToTemporaryTableIfYouAreAlreadyUsingIt() throws Exception {
		Set<Integer> pIds = new HashSet<Integer>(Arrays.asList(2,7));
		evaluationService.startUsing(pIds);
		checkNumIdsPersisted(2);
		evaluationService.startUsing(pIds);
		checkNumIdsPersisted(2);
		evaluationService.stopUsing(evaluationService.generateKey(pIds));
	}

	@Test
	public void isInUse_shouldReturnTrueIfIdSetIsInUse() throws Exception {
		Set<Integer> pIds = new HashSet<Integer>(Arrays.asList(2,7));
		String key = evaluationService.generateKey(pIds);
		Assert.assertFalse(evaluationService.isInUse(key));
		evaluationService.startUsing(pIds);
		Assert.assertTrue(evaluationService.isInUse(key));
		evaluationService.stopUsing(key);
		Assert.assertFalse(evaluationService.isInUse(key));
	}

	@Test
	public void stopUsing_shouldRemoveIdSetFromTemporaryTableIfEveryoneIsDoneUsingIt() throws Exception {
		Set<Integer> pIds = new HashSet<Integer>(Arrays.asList(2,7));
		String key = evaluationService.generateKey(pIds);
		Assert.assertFalse(evaluationService.isInUse(key));
		evaluationService.startUsing(pIds);
		Assert.assertTrue(evaluationService.isInUse(key));
		evaluationService.stopUsing(key);
		Assert.assertFalse(evaluationService.isInUse(key));
	}

	@Test
	public void stopUsing_shouldNotRemoveIdSetFromTemporaryTableIfSomeoneIsUsingIt() throws Exception {
		Set<Integer> pIds = new HashSet<Integer>(Arrays.asList(2,7));
		String key = evaluationService.generateKey(pIds);
		Assert.assertFalse(evaluationService.isInUse(key));
		evaluationService.startUsing(pIds);
		evaluationService.startUsing(pIds);
		Assert.assertTrue(evaluationService.isInUse(key));
		evaluationService.stopUsing(key);
		Assert.assertTrue(evaluationService.isInUse(key));
		evaluationService.stopUsing(key);
		Assert.assertFalse(evaluationService.isInUse(key));
	}

	@Test
	public void resetAllIdSets_shouldRemoveAnyIdSet() throws Exception {
		evaluationService.startUsing(new HashSet<Integer>(Arrays.asList(2,7)));
		evaluationService.startUsing(new HashSet<Integer>(Arrays.asList(1,8)));
		evaluationService.startUsing(new HashSet<Integer>(Arrays.asList(2,7,1,8)));
		org.openmrs.test.TestUtil.printOutTableContents(getConnection(), "reporting_idset");
		checkNumIdsPersisted(8);
		evaluationService.resetAllIdSets();
		checkNumIdsPersisted(0);
	}

	protected void checkNumIdsPersisted(int numExpected) {
		String hql = "select memberId from IdsetMember";
		List ret = sessionFactory.getCurrentSession().createQuery(hql).list();
		Assert.assertEquals("Expected " + numExpected + " but got " + ret.size() + ": " + ret, numExpected, ret.size());
	}

	protected void checkIdSetMembers(String key, Integer...expectedValues) {
		String hql = "select memberId from IdsetMember" + (key == null ? "" : " where key = '" + key + "'") + " order by memberId";
		List ret = sessionFactory.getCurrentSession().createQuery(hql).list();
		if (expectedValues == null || expectedValues.length == 0) {
			Assert.assertEquals(0, ret.size());
			Assert.assertFalse(evaluationService.isInUse(key));
		}
		else {
			for (int i = 0; i < expectedValues.length; i++) {
				Assert.assertEquals(expectedValues[i], ret.get(i));
			}
			Assert.assertTrue(evaluationService.isInUse(key));
		}
	}
}