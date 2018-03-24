/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.calculation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientDataCalculationBehaviorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	private PatientService ps;
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		ps = Context.getPatientService();
	}
	
	@Test
	public void evaluate_shouldEvaluateAPatientCalculation() throws Exception {
		Integer patientId1 = 2;
		Integer patientId2 = 7;
		Set<PatientIdentifier> identifiers1 = ps.getPatient(patientId1).getIdentifiers();
		Set<PatientIdentifier> identifiers2 = ps.getPatient(patientId2).getIdentifiers();
		PatientDataCalculation calculation = new PatientDataCalculationProvider().getCalculation(
		    "org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition", null);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("types", ps.getAllPatientIdentifierTypes(false));
		
		CalculationResultMap results = calculation.evaluate(Arrays.asList(patientId1, patientId2), parameters, null);
		
		Assert.assertEquals(identifiers2.iterator().next(), ResultUtil.getFirst(results.get(patientId2)).getValue());
		
		ListResult lr = (ListResult) results.get(patientId1);
		Assert.assertEquals(3, lr.size());
		
		Assert.assertTrue(CollectionUtils.isEqualCollection(identifiers1, lr.getValues()));
	}
	
	@Test
	public void evaluate_shouldEvaluateAPatientCalculationWithTheSpecifiedParameterValues() throws Exception {
		Integer patientId1 = 2;
		Integer patientId2 = 7;
		PatientIdentifierType type = Context.getPatientService().getPatientIdentifierType(1);
		PatientIdentifier id1 = ps.getPatient(patientId1).getPatientIdentifier(type);
		PatientIdentifier id2 = ps.getPatient(patientId2).getPatientIdentifier(type);
		PatientDataCalculation calculation = new PatientDataCalculationProvider().getCalculation(
		    "org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition", null);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("types", Collections.singletonList(type));
		parameters.put("includeFirstNonNullOnly", true);
		
		CalculationResultMap results = calculation.evaluate(Arrays.asList(patientId1, patientId2), parameters, null);
		
		Assert.assertEquals(id1, ResultUtil.getFirst(results.get(patientId1)).getValue());
		Assert.assertEquals(id2, ResultUtil.getFirst(results.get(patientId2)).getValue());
	}
	
	@Test
	public void evaluate_shouldEvaluateTheSpecifiedPersonCalculation() throws Exception {
		Integer patientId1 = 2;
		Integer patientId2 = 7;
		String gender1 = ps.getPatient(patientId1).getGender();
		String gender2 = ps.getPatient(patientId2).getGender();
		PatientDataCalculation calculation = new PatientDataCalculationProvider().getCalculation(
		    "org.openmrs.module.reporting.data.person.definition.GenderDataDefinition", null);
		
		CalculationResultMap results = calculation.evaluate(Arrays.asList(patientId1, patientId2), null, null);
		
		Assert.assertEquals(gender1, ResultUtil.getFirst(results.get(patientId1)).getValue());
		Assert.assertEquals(gender2, ResultUtil.getFirst(results.get(patientId2)).getValue());
	}
}
