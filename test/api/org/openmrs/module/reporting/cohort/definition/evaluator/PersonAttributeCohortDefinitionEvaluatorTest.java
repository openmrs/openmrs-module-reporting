package org.openmrs.module.reporting.cohort.definition.evaluator;


import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * 
 */
public class PersonAttributeCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
	 * Logger
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	public Boolean useInMemoryDatabase() {
		return true;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}

	/**
	 * @see {@link PersonAttributeCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should get patients having attributes with given attribute type and values", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldGetPatientsWithGivenAttributeTypeAndValues() throws Exception {		
		PersonAttributeCohortDefinition pacd = new PersonAttributeCohortDefinition();		
		pacd.setAttributeType(new PersonAttributeType(8)); 
		pacd.setValues(Arrays.asList("5"));	
		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(pacd, null);
		Assert.assertEquals(2, cohort.size());
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(7));
	}
	
	/**
	 * Should match all patients with any person attribute type.
	 * 
	 * @see {@link PersonAttributeCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should get patients having any attributes", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldGetPatientsHavingAnyAttributes() throws Exception {
		
		// Get all patients with at least one person attribute of any type 
		PersonAttributeCohortDefinition pacd = new PersonAttributeCohortDefinition();
		pacd.setAttributeType(null);
		pacd.setValues(null);
		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(pacd, null);
		Assert.assertEquals(4, cohort.size());
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(6));
		Assert.assertTrue(cohort.contains(7));
		Assert.assertTrue(cohort.contains(8));
	}

	/**
	 * @see {@link PersonAttributeCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should get patients having attributes with any given attribute values", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldGetPatientsHavingAttributesWithAnyGivenAttributeValues() throws Exception {
		PersonAttributeCohortDefinition pacd = new PersonAttributeCohortDefinition();
		pacd.setValues(Arrays.asList("Boston, MA", "New York, NY"));		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(pacd, null);		
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(8));
	}


}