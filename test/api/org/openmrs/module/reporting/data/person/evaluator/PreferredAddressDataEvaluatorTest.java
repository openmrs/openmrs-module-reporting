package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PreferredAddressDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see PreferredNameDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the most preferred address for each person in the passed context
	 */
	@Test
	public void evaluate_shouldReturnMostPreferredAddressForAllPersons() throws Exception {
		PreferredAddressDataDefinition d = new PreferredAddressDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,7,8"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals("1050 Wishard Blvd.", ((PersonAddress)pd.getData().get(2)).getAddress1());
		Assert.assertEquals("Kapina", ((PersonAddress)pd.getData().get(7)).getCityVillage());
		Assert.assertEquals("Jabali", ((PersonAddress)pd.getData().get(8)).getCityVillage());
	}
}