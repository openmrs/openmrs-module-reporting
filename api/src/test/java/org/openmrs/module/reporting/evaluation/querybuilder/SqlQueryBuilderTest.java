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
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Tests for the EvaluationContext expression parsing
 */
public class SqlQueryBuilderTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	EvaluationService evaluationService;

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void getColumns_shouldReturnTheConfiguredColumns() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select p.person_id id, p.gender, p.birthdate as bd from person p where voided = 0");
		List<DataSetColumn> columns = evaluationService.getColumns(q);
		Assert.assertEquals("id", columns.get(0).getName().toLowerCase());
		Assert.assertEquals("gender", columns.get(1).getName().toLowerCase());
		Assert.assertEquals("bd", columns.get(2).getName().toLowerCase());
	}

	@Test
	public void buildQuery_shouldHandleNoParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select p.person_id, p.gender, p.birthdate as bd from person p where person_id = 2");
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(1, result.size());
		Object[] row = result.get(0);
		Assert.assertEquals(2, row[0]);
		Assert.assertEquals("M", row[1]);
		Assert.assertEquals(DateUtil.getDateTime(1975,4,8), row[2]);
	}

	@Test
	public void buildQuery_shouldHandleSimpleParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select p.person_id from person p where gender = :g");
		q.addParameter("g", "M");
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(5, result.size());
		q.addParameter("g", "F");
		result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(6, result.size());
	}

	@Test
	public void buildQuery_shouldHandleOpenmrsObjectParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select e.encounter_id from encounter e where e.encounter_type = :type");
		q.addParameter("type", Context.getEncounterService().getEncounterType("Scheduled"));
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void buildQuery_shouldHandleListParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select e.encounter_datetime, e.encounter_type from encounter e where e.encounter_id in (:ids)");
		q.addParameter("ids", Arrays.asList(3,4,5,6));
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(4, result.size());
	}

	@Test
	public void buildQuery_shouldHandleListsOfOpenmrsObjectParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select e.encounter_datetime from encounter e where e.encounter_type in (:types)");
		List<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(Context.getEncounterService().getEncounterType("Scheduled"));
		typeList.add(Context.getEncounterService().getEncounterType("Emergency"));
		q.addParameter("types", typeList);
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void buildQuery_shouldHandleCohortParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select p.gender from person p where p.person_id in (:cohort)");
		Cohort baseCohort = new Cohort("2,6,7");
		q.addParameter("cohort", baseCohort);
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void buildQuery_shouldHandleComments() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("-- This query selects genders for the given cohort\n");
		q.append("select p.gender from person p where p.person_id in (:cohort)");
		Cohort baseCohort = new Cohort("2,6,7");
		q.addParameter("cohort", baseCohort);
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void buildQuery_shouldSupportParametersThatStartWithSameSequence() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select patient_id from patient where patient_id = :patient24 and patient_id <> :patient2");
		q.addParameter("patient2", 2);
		q.addParameter("patient24", 24);
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void buildQuery_shouldSupportMultipleParametersWithSameName() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		String repeatingClause = "(value_coded = :concept and value_datetime > :fromDate and value_datetime < :toDate)";
		q.append("select obs_id from obs where ").append(repeatingClause);
		for (int i=0; i<100; i++) {
			q.append(" and ").append(repeatingClause);
		}
		q.addParameter("concept", 5097);
		q.addParameter("fromDate", DateUtil.getDateTime(2011, 1, 1));
		q.addParameter("toDate", DateUtil.getDateTime(2011, 12, 31));
		List<Object[]> result = evaluationService.evaluateToList(q, new EvaluationContext());
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		//p.setProperty("hibernate.show_sql", "true");
		return p;
	}
}
