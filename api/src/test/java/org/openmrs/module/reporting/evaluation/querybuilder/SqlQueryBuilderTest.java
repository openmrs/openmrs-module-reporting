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
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
		List<DataSetColumn> columns = q.getColumns();
		Assert.assertEquals("id", columns.get(0).getName());
		Assert.assertEquals("gender", columns.get(1).getName());
		Assert.assertEquals("bd", columns.get(2).getName());
	}

	@Test
	public void buildQuery_shouldHandleNoParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select p.person_id, p.gender, p.birthdate as bd from person p where person_id = 2");
		List<Object[]> result = evaluationService.evaluateToList(q);
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
		List<Object[]> result = evaluationService.evaluateToList(q);
		Assert.assertEquals(5, result.size());
		q.addParameter("g", "F");
		result = evaluationService.evaluateToList(q);
		Assert.assertEquals(6, result.size());
	}

	@Test
	public void buildQuery_shouldHandleOpenmrsObjectParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select e.encounter_id from encounter e where e.encounter_type = :type");
		q.addParameter("type", Context.getEncounterService().getEncounterType("Scheduled"));
		List<Object[]> result = evaluationService.evaluateToList(q);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void buildQuery_shouldHandleListParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select e.encounter_datetime, e.encounter_type from encounter e where e.encounter_id in (:ids)");
		q.addParameter("ids", Arrays.asList(3,4,5,6));
		List<Object[]> result = evaluationService.evaluateToList(q);
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
		List<Object[]> result = evaluationService.evaluateToList(q);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void buildQuery_shouldHandleCohortParameters() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select p.gender from person p where p.person_id in (:cohort)");
		Cohort baseCohort = new Cohort("2,6,7");
		q.addParameter("cohort", baseCohort);
		List<Object[]> result = evaluationService.evaluateToList(q);
		Assert.assertEquals(3, result.size());
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		//p.setProperty("hibernate.show_sql", "true");
		return p;
	}
}
