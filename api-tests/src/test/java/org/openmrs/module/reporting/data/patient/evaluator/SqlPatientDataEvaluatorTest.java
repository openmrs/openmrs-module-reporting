/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	PatientDataService patientDataService;

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void testEvaluate() throws Exception {
		String sql = "select p.patient_id, p.date_created from patient p where p.patient_id in (:patientIds)";
		SqlPatientDataDefinition definition = new SqlPatientDataDefinition();
		definition.setSql(sql);

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,7"));
		EvaluatedPatientData data = patientDataService.evaluate(definition, context);

		assertThat(data.getData().size(), is(2));
		testDate(data, 2, DateUtil.getDateTime(2005,9,22));
		testDate(data, 7, DateUtil.getDateTime(2006,1,18));
	}

	public void testDate(PatientData data, Integer pId, Date expected) {
		Date d = (Date)data.getData().get(pId);
		Assert.assertEquals(expected.getTime(), d.getTime());
	}
}