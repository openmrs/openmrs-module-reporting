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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class PatientIdentifierDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PatientIdentifierDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return all identifiers of the specified types in order for each patient
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void evaluate_shouldReturnAllIdentifiersOfTheSpecifiedTypesInOrderForEachPatient() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();
		d.addType(Context.getPatientService().getPatientIdentifierType(1)); // "101-6", preferred
		d.addType(Context.getPatientService().getPatientIdentifierType(2)); // "101"
		
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		
		Object o = pd.getData().get(2);
		List<PatientIdentifier> identifiers = (List<PatientIdentifier>) o;
		Assertions.assertEquals(3, identifiers.size());
		Assertions.assertEquals("101-6", identifiers.get(0).getIdentifier());
		Assertions.assertEquals("102", identifiers.get(1).getIdentifier());
		Assertions.assertEquals("101", identifiers.get(2).getIdentifier());

        d.setIncludeFirstNonNullOnly(true);
        pd = Context.getService(PatientDataService.class).evaluate(d, context);
        o = pd.getData().get(2);
        Assertions.assertEquals("101-6", ((PatientIdentifier)o).getIdentifier());
	}

	/**
	 * @verifies return all identifiers in groups according to preferred list order
	 * @see PatientIdentifierDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnAllIdentifiersInGroupsAccordingToPreferredListOrder() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		PatientIdentifierType pi1 = Context.getPatientService().getPatientIdentifierType(1);
		PatientIdentifierType pi2 = Context.getPatientService().getPatientIdentifierType(2);

		PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();
		d.addType(pi2); // "101"
		d.addType(pi1); // "101-6", preferred

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);

		Object o = pd.getData().get(2);
		List<PatientIdentifier> identifiers = (List<PatientIdentifier>) o;
		Assertions.assertEquals(3, identifiers.size());
		Assertions.assertEquals(pi2, identifiers.get(0).getIdentifierType());
		Assertions.assertEquals(pi2, identifiers.get(1).getIdentifierType());
		Assertions.assertEquals(pi1, identifiers.get(2).getIdentifierType());
	}

    /**
     * @verifies return all identifiers in groups according to preferred list order
     * @see PatientIdentifierDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
     */
    @Test
    public void evaluate_shouldReturnAllIdentifiersIfNoTypeSpecified() throws Exception {
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort("2"));

        PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();

        EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);

        Object o = pd.getData().get(2);
        List<PatientIdentifier> identifiers = (List<PatientIdentifier>) o;
        Assertions.assertEquals(3, identifiers.size());
    }

	/**
	 * @verifies place all preferred identifiers first within type groups
	 * @see PatientIdentifierDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldPlaceAllPreferredIdentifiersFirstWithinTypeGroups() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		PatientIdentifierType pi1 = Context.getPatientService().getPatientIdentifierType(1);
		PatientIdentifierType pi2 = Context.getPatientService().getPatientIdentifierType(2);

		PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();
		d.addType(pi2); // "101"
		d.addType(pi1); // "101-6", preferred

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);

		Object o = pd.getData().get(2);
		List<PatientIdentifier> identifiers = (List<PatientIdentifier>) o;
		Assertions.assertEquals(3, identifiers.size());
		Assertions.assertEquals(Boolean.TRUE, identifiers.get(0).getPreferred());
		Assertions.assertEquals(Boolean.FALSE, identifiers.get(1).getPreferred());
		Assertions.assertEquals(Boolean.TRUE, identifiers.get(2).getPreferred());
	}
}