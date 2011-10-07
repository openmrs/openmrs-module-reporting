package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonAttributeDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see PersonAttributeDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the person attribute of the given type for each person
	 */
	@Test
	public void evaluate_shouldReturnAllBirthDatesForAllPersons() throws Exception {
		PersonAttributeDataDefinition d = new PersonAttributeDataDefinition();
		d.setType(Context.getPersonService().getPersonAttributeType(2));
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("6,7,8"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals("Jamaica", ((PersonAttribute)pd.getData().get(6)).getHydratedObject());
		Assert.assertEquals("Paris, France", ((PersonAttribute)pd.getData().get(7)).getHydratedObject());
		Assert.assertEquals("Boston, MA", ((PersonAttribute)pd.getData().get(8)).getHydratedObject());
	}
}