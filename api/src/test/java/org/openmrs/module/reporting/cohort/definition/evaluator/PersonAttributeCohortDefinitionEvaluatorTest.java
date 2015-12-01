package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the PersonAttributeCohortDefinitionEvaluator
 */
public class PersonAttributeCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
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

	/**
	 * @see {@link PersonAttributeCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should get patients having attributes with concept attribute values", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldGetPatientsHavingAttributesWithLocationAttributeValues() throws Exception {
		PersonAttributeCohortDefinition pacd = new PersonAttributeCohortDefinition();
		pacd.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Civil Status"));
		List<Concept> civilStatuses = new ArrayList<Concept>();
		civilStatuses.add(Context.getConceptService().getConceptByName("MARRIED"));
		pacd.setValueConcepts(civilStatuses);		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(pacd, null);		
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(8));
	}
}