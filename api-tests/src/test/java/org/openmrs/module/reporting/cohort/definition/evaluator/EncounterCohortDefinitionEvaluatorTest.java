package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class EncounterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
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
		Assert.assertEquals(8, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(20));
		Assert.assertTrue(c.contains(21));
		Assert.assertTrue(c.contains(22));
		Assert.assertTrue(c.contains(23));
		Assert.assertTrue(c.contains(24));
		Assert.assertTrue(c.contains(101));
		Assert.assertTrue(c.contains(102));
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
	@Ignore
	@Test
	public void evaluate_shouldReturnCorrectPatientsWhenProviderParametersAreSet() throws Exception {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.addProvider(new Person(2));
		Assert.assertEquals(2, DefinitionContext.getCohortDefinitionService().evaluate(cd, new EvaluationContext()).size());
	}
	
	/**
	 * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Ignore
	@Test
	@Verifies(value = "should not return voided patients", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldNotReturnVoidedPatients() throws Exception {
		
		Patient patient = Context.getPatientService().getPatient(7);
		Context.getPatientService().voidPatient(patient, "testing");
		Context.flushSession();
		
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(new ArrayList<EncounterType>()); // this is a regression test for a NPE on empty lists
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(5, c.size());
		Assert.assertFalse(c.contains(7));
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
	@Verifies(value = "should find patients with encounters on the onOrBefore date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsWithEncountersOnTheOnOrBeforeDateIfPassedInTimeIsAtMidnight() throws Exception {
		EncounterService es = Context.getEncounterService();
		Encounter enc = es.getEncounter(3);
		final Integer patentId = 7;
		Assert.assertEquals(patentId, enc.getPatient().getPatientId());//sanity check
		enc.setEncounterDatetime(DateUtil.getDateTime(2005, 8, 1, 11, 0, 0, 0));
		es.saveEncounter(enc);
		Context.flushSession();//because the query will compare with the value in the DB
		
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setOnOrBefore(DateUtil.getDateTime(2005, 8, 1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patentId));
	}
	
	/**
	 * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients with encounters created on the specified date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsWithEncountersCreatedOnTheSpecifiedDateIfPassedInTimeIsAtMidnight()
	    throws Exception {
		EncounterService es = Context.getEncounterService();
		Encounter enc = es.getEncounter(3);
		final Integer patentId = 7;
		Assert.assertEquals(patentId, enc.getPatient().getPatientId());
		enc.setDateCreated(DateUtil.getDateTime(2005, 8, 1, 11, 0, 0, 0));
		es.saveEncounter(enc);
		Context.flushSession();
		
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setCreatedOnOrBefore(DateUtil.getDateTime(2005, 8, 1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patentId));
	}

	@Test
	public void evaluate_shouldFollowChildLocationsIfIncludeChildLocationsIsTrue() throws Exception {

		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setIncludeChildLocations(true);
		cd.addLocation(Context.getLocationService().getLocation(4));
		Location location = Context.getLocationService().getLocation(4);
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, c.getSize());
		Assert.assertTrue(c.contains(101));
		Assert.assertTrue(c.contains(102));
	}

	@Test
	public void evaluate_shouldNotFollowChildLocationsIfIncludeChildLocationsIsFalse() throws Exception {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setIncludeChildLocations(false);
		cd.addLocation(Context.getLocationService().getLocation(4));
		Location location = Context.getLocationService().getLocation(4);
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, c.getSize());
		Assert.assertTrue(c.contains(101));
	}
}
