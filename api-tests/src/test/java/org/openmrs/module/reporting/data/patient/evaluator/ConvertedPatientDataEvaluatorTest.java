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
package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Map;

public class ConvertedPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

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

	/**
	 * @see org.openmrs.module.reporting.data.patient.evaluator.ConvertedPatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @verifies return all identifiers of the specified types in order for each patient
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void evaluate_shouldReturnConvertedData() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		PreferredIdentifierDataDefinition d = new PreferredIdentifierDataDefinition();
		d.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));

		ConvertedPatientDataDefinition cd = new ConvertedPatientDataDefinition();
		cd.setDefinitionToConvert(new Mapped<PatientDataDefinition>(d, null));

		PropertyConverter pc = new PropertyConverter();
		pc.setPropertyName("identifier");
		cd.addConverter(pc);
		
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(cd, context);
		
		Object o = pd.getData().get(2);
		Assert.assertEquals(String.class, o.getClass());
		Assert.assertEquals("101-6", o);
	}

	/**
	 * @see org.openmrs.module.reporting.data.patient.evaluator.ConvertedPatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @verifies return all identifiers of the specified types in order for each patient
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void evaluate_shouldSupportChangingParameterNames() throws Exception {

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		context.addParameterValue("idType", Context.getPatientService().getPatientIdentifierType(1));

		PreferredIdentifierDataDefinition d = new PreferredIdentifierDataDefinition();
		d.addParameter(new Parameter("identifierType", "identifierType", PatientIdentifierType.class));

		ConvertedPatientDataDefinition cd = new ConvertedPatientDataDefinition();
		cd.addParameter(new Parameter("idType", "idType", PatientIdentifierType.class));

		Map<String, Object> mappings = ParameterizableUtil.createParameterMappings("identifierType=${idType}");
		cd.setDefinitionToConvert(new Mapped<PatientDataDefinition>(d, mappings));

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(cd, context);

		Object o = pd.getData().get(2);
		Assert.assertEquals(PatientIdentifier.class, o.getClass());
	}
}