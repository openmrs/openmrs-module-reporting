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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import java.util.Map;

public class ConvertedPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link org.openmrs.test.jupiter.BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@BeforeEach
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
		Assertions.assertEquals(String.class, o.getClass());
		Assertions.assertEquals("101-6", o);
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
		Assertions.assertEquals(PatientIdentifier.class, o.getClass());
	}
}