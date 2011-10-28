package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;


public class EncounterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
     * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should return all patients with encounters if all arguments to cohort definition are empty", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnAllPatientsWithEncountersIfAllArgumentsToCohortDefinitionAreEmpty() throws Exception {
	   EncounterCohortDefinition cd = new EncounterCohortDefinition();
	   cd.setEncounterTypeList(new ArrayList<EncounterType>()); // this is a regression test for a NPE on empty lists
	   Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
	   Assert.assertEquals(6, c.size());
	   Assert.assertTrue(c.contains(7));
 	   Assert.assertTrue(c.contains(20));
 	   Assert.assertTrue(c.contains(21));
 	   Assert.assertTrue(c.contains(22));
 	   Assert.assertTrue(c.contains(23));
 	   Assert.assertTrue(c.contains(24));
    }

	/**
     * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should return correct patients when all non grouping parameters are set", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnCorrectPatientsWhenAllNonGroupingParametersAreSet() throws Exception {
       EncounterCohortDefinition cd = new EncounterCohortDefinition();
 	   cd.setEncounterTypeList(Collections.singletonList(new EncounterType(6)));
 	   cd.setFormList(Collections.singletonList(new Form(1)));
 	   cd.setLocationList(Collections.singletonList(new Location(2)));
 	   cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 19));
 	   cd.setOnOrBefore(DateUtil.getDateTime(2009, 8, 19));
 	   Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
 	   Assert.assertEquals(3, c.size());
 	   Assert.assertTrue(c.contains(20));
 	   Assert.assertTrue(c.contains(21));
 	   Assert.assertTrue(c.contains(23));
    }

	/**
     * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     * 
     */
    @Test
    @Verifies(value = "should return correct patients when all parameters are set", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnCorrectPatientsWhenAllParametersAreSet() throws Exception {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.setEncounterTypeList(Collections.singletonList(new EncounterType(6)));
        cd.setFormList(Collections.singletonList(new Form(1)));
        cd.setLocationList(Collections.singletonList(new Location(2)));
        cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 19));
        cd.setOnOrBefore(DateUtil.getDateTime(2009, 8, 19));
        cd.setAtLeastCount(1);
        cd.setAtMostCount(1);
        Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
        Assert.assertEquals(3, c.size());
        Assert.assertTrue(c.contains(20));
        Assert.assertTrue(c.contains(21));
        Assert.assertTrue(c.contains(23));
    }
	
	/**
     * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should return correct patients when creation date parameters are set", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnCorrectPatientsWhenCreationDateParametersAreSet() throws Exception {
    	
    	// If parameter dates have no time components, they should return all encounters on that date
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setCreatedOnOrAfter(DateUtil.getDateTime(2008, 8, 19));
	        cd.setCreatedOnOrBefore(DateUtil.getDateTime(2008, 8, 19));
	        Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
	        Assert.assertEquals(6, c.size());
    	}

    	// If parameter dates do have time components, they should return all encounters between the specific datetimes
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setCreatedOnOrAfter(DateUtil.getDateTime(2008, 8, 19, 11, 30, 0, 0));
	        cd.setCreatedOnOrBefore(DateUtil.getDateTime(2008, 8, 19, 14, 30, 0, 0));
	        Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
	        Assert.assertEquals(3, c.size());
    	}
    }
    
	/**
     * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should return correct patients when time qualifier parameters are set", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnCorrectPatientsWhenTimeQualifierParametersAreSet() throws Exception {

    	EvaluationContext context = new EvaluationContext();

    	// None specified use case
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setEncounterTypeList(Arrays.asList(new EncounterType(6)));
	        cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 1));
	        cd.setOnOrBefore(DateUtil.getDateTime(2009, 8, 31));
	        Assert.assertEquals(3, DefinitionContext.getCohortDefinitionService().evaluate(cd, context).size());
    	}
    	
    	// Any use case
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setTimeQualifier(TimeQualifier.ANY);
	        cd.setEncounterTypeList(Arrays.asList(new EncounterType(6)));
	        cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 1));
	        cd.setOnOrBefore(DateUtil.getDateTime(2009, 8, 31));
	        Assert.assertEquals(3, DefinitionContext.getCohortDefinitionService().evaluate(cd, context).size());
    	}
    	
    	// First use case
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setTimeQualifier(TimeQualifier.FIRST);
	        cd.setEncounterTypeList(Arrays.asList(new EncounterType(6)));
	        cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 1));
	        cd.setOnOrBefore(DateUtil.getDateTime(2009, 8, 31));
	        Assert.assertEquals(3, DefinitionContext.getCohortDefinitionService().evaluate(cd, context).size());
    	}
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setTimeQualifier(TimeQualifier.FIRST);
	        cd.setEncounterTypeList(Arrays.asList(new EncounterType(6)));
	        cd.setOnOrAfter(DateUtil.getDateTime(2009, 9, 1));
	        cd.setOnOrBefore(DateUtil.getDateTime(2009, 9, 30));
	        Assert.assertEquals(2, DefinitionContext.getCohortDefinitionService().evaluate(cd, context).size());
    	}
    	
    	// Last use case
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setTimeQualifier(TimeQualifier.LAST);
	        cd.setEncounterTypeList(Arrays.asList(new EncounterType(6)));
	        cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 1));
	        cd.setOnOrBefore(DateUtil.getDateTime(2009, 8, 31));
	        Assert.assertEquals(2, DefinitionContext.getCohortDefinitionService().evaluate(cd, context).size());
    	}
    	{
	        EncounterCohortDefinition cd = new EncounterCohortDefinition();
	        cd.setTimeQualifier(TimeQualifier.LAST);
	        cd.setEncounterTypeList(Arrays.asList(new EncounterType(6)));
	        cd.setOnOrAfter(DateUtil.getDateTime(2009, 9, 1));
	        cd.setOnOrBefore(DateUtil.getDateTime(2009, 9, 30));
	        Assert.assertEquals(2, DefinitionContext.getCohortDefinitionService().evaluate(cd, context).size());
    	}
    }

	/**
	 * @see EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)
	 * @verifies return correct patients when provider parameters are set
	 */
	@Test
	public void evaluate_shouldReturnCorrectPatientsWhenProviderParametersAreSet() throws Exception {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.addProvider(new Person(2));
        Assert.assertEquals(2, DefinitionContext.getCohortDefinitionService().evaluate(cd, new EvaluationContext()).size());
	}
}
