/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class SimplePatientDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link SimplePatientDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a SimplePatientDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateASimplePatientDataSetDefinition() throws Exception {
		
		SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
		d.addIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName("Old Identification Number"));
		d.addPatientProperty("patientId");
		d.addPatientProperty("givenName");
		d.addPatientProperty("familyName");
		d.addPatientProperty("gender");
		d.addPatientProperty("age");
		d.addPersonAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Birthplace"));
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assertions.assertEquals(9, result.getRows().size());
		Assertions.assertEquals(7, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			Integer patientId = (Integer)row.getColumnValue("patientId");
			Patient p = Context.getPatientService().getPatient(patientId);
			Assertions.assertTrue(ObjectUtil.areEqualStr(p.getPatientIdentifier("Old Identification Number"), row.getColumnValue("Old Identification Number")));
			Assertions.assertEquals(p.getGivenName(), row.getColumnValue("givenName"));
			Assertions.assertEquals(p.getFamilyName(), row.getColumnValue("familyName"));
			Assertions.assertEquals(p.getGender(), row.getColumnValue("gender"));
			Assertions.assertEquals(p.getAge(), row.getColumnValue("age"));
			Object attVal = p.getAttribute("Birthplace") == null ? null : p.getAttribute("Birthplace").getHydratedObject();
			Assertions.assertEquals(attVal, row.getColumnValue("Birthplace"));
		}
	}
}