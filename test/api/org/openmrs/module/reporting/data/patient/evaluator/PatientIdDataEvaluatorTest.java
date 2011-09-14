package org.openmrs.module.reporting.data.patient.evaluator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientIdDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see PatientIdDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patientIds for all patients in the the passed context
	 */
	@Test
	public void evaluate_shouldReturnPatientIdsForAllPatientsInTheThePassedContext() throws Exception {
		
		// Test for all patients
		PatientIdDataDefinition d = new PatientIdDataDefinition();
		EvaluationContext context = new EvaluationContext();
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(9, pd.getData().size());
		for (Integer pId : pd.getData().keySet()) {
			Assert.assertEquals(pId, pd.getData().get(pId));
		}
		
		// Test for a limited base cohort of patients
		context.setBaseCohort(new Cohort("2,6,7,8"));
		pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(4, pd.getData().size());
	}
}