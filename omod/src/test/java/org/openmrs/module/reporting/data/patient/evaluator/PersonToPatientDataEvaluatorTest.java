package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonToPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see PersonToPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return person data by for each patient in the passed cohort
	 */
	@Test
	public void evaluate_shouldReturnPersonDataByForEachPatientInThePassedCohort() throws Exception {
		PersonToPatientDataDefinition d = new PersonToPatientDataDefinition(new BirthdateDataDefinition());
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(4, pd.getData().size());
		BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
		Assert.assertEquals("1975-04-08", c.convert(pd.getData().get(2)));
		Assert.assertEquals("2007-05-27", c.convert(pd.getData().get(6)));
		Assert.assertEquals("1976-08-25", c.convert(pd.getData().get(7)));
		Assert.assertNull(pd.getData().get(8));
	}
}