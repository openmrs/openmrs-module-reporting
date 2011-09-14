package org.openmrs.module.reporting.data.patient.evaluator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
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
	 * @verifies return the preferred identifier of the passed type for each patient in the passed context
	 */
	@Test
	public void evaluate_shouldReturnThePreferredIdentifierOfThePassedTypeForEachPatientInThePassedContext() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));
		
		PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();
		d.setType(Context.getPatientService().getPatientIdentifierType(1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		
		Assert.assertEquals(3, pd.getData().size()); // TODO: Is this what we want, or do we want all 4 patients returned, with potential null results?
		Assert.assertEquals("101-6", pd.getData().get(2));
		Assert.assertNull(pd.getData().get(6));
		Assert.assertEquals("6TS-4", pd.getData().get(7));
		Assert.assertEquals("7TU-8", pd.getData().get(8));
	}
}