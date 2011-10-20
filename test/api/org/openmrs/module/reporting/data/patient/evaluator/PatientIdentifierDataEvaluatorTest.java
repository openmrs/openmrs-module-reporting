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

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientIdentifierDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
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
		d.addType(Context.getPatientService().getPatientIdentifierType(1)); // "101"
		d.addType(Context.getPatientService().getPatientIdentifierType(2)); // "101-6", preferred
		
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		
		Object o = pd.getData().get(2);
		List<PatientIdentifier> identifiers = (List<PatientIdentifier>) o;
		Assert.assertEquals(2, identifiers.size());
		Assert.assertEquals("101", identifiers.get(0).getIdentifier());
		Assert.assertEquals("101-6", identifiers.get(1).getIdentifier());

        d.setIncludeFirstNonNullOnly(true);
        pd = Context.getService(PatientDataService.class).evaluate(d, context);
        o = pd.getData().get(2);
        Assert.assertEquals("101", ((PatientIdentifier)o).getIdentifier());
	}
}