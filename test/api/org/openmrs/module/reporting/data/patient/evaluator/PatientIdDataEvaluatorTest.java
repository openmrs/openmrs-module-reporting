package org.openmrs.module.reporting.data.patient.evaluator;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public class PatientIdDataEvaluatorTest {
	
	/**
	 * @see PatientIdDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patientIds for all patients in the the passed context
	 */
	@Test
	public void evaluate_shouldReturnPatientIdsForAllPatientsInTheThePassedContext() throws Exception {
		
		PatientIdDataDefinition d = new PatientIdDataDefinition();
		EvaluationContext context = new EvaluationContext();
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(9, pd.getData().size());
	}
}