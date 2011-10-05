package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class BirthdateDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see BirthdateDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return all birth dates for all persons
	 */
	@Test
	public void evaluate_shouldReturnAllBirthDatesForAllPersons() throws Exception {
		BirthdateDataDefinition d = new BirthdateDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(4, pd.getData().size());
		Assert.assertEquals("1975-04-08", DateUtil.formatDate(((Birthdate)pd.getData().get(2)).getBirthdate(), "yyyy-MM-dd"));
		Assert.assertEquals("2007-05-27", DateUtil.formatDate(((Birthdate)pd.getData().get(6)).getBirthdate(), "yyyy-MM-dd"));
		Assert.assertEquals("1976-08-25", DateUtil.formatDate(((Birthdate)pd.getData().get(7)).getBirthdate(), "yyyy-MM-dd"));
		Assert.assertNull(pd.getData().get(8));
	}
}