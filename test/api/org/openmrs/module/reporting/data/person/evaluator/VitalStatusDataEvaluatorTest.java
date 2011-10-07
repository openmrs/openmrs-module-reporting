package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class VitalStatusDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see BirthdateDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return vital status for all persons
	 */
	@Test
	public void evaluate_shouldReturnVitalStatusForAllPersons() throws Exception {
		VitalStatusDataDefinition d = new VitalStatusDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("20,21"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(2, pd.getData().size());
		VitalStatus deadStatus = (VitalStatus)pd.getData().get(20);
		Assert.assertEquals(true, deadStatus.getDead());
		Assert.assertEquals("2005-02-08", DateUtil.formatDate(deadStatus.getDeathDate(), "yyyy-MM-dd"));
		VitalStatus alive = (VitalStatus)pd.getData().get(21);
		Assert.assertEquals(false, alive.getDead());
		Assert.assertNull(alive.getDeathDate());
	}
}