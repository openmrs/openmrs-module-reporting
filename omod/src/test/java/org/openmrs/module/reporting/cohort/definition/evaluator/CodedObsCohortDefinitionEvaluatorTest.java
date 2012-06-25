package org.openmrs.module.reporting.cohort.definition.evaluator;


import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CodedObsCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link CodedObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 * 
	 */
	@Test
	@Verifies(value = "should test any with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestAnyWithManyPropertiesSpecified() throws Exception {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(21)); // FOOD ASSISTANCE FOR ENTIRE FAMILY, in the reporting test dataset
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Collections.singletonList(new Concept(7))); // YES, in the reporting test dataset
		cd.setOnOrAfter(DateUtil.getDateTime(2008, 8, 14));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 16));
		cd.setLocationList(Collections.singletonList(new Location(1)));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
	}
	
	/**
	 * @see {@link CodedObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 * 
	 */
	@Test
	@Verifies(value = "should test last with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestLastWithManyPropertiesSpecified() throws Exception {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.LAST);
		cd.setQuestion(new Concept(21)); // FOOD ASSISTANCE FOR ENTIRE FAMILY, in the reporting test dataset
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Collections.singletonList(new Concept(7))); // YES, in the reporting test dataset
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 16));
		cd.setLocationList(Collections.singletonList(new Location(1)));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
	}
	
	/**
	 * @see {@link CodedObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test that obs are retricted by encounter type", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestThatObsAreRestrictedByEncounterType() throws Exception {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.LAST);
		cd.setQuestion(new Concept(21)); // FOOD ASSISTANCE FOR ENTIRE FAMILY, in the reporting test dataset
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Collections.singletonList(new Concept(7))); // YES, in the reporting test dataset
		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());

		cd.setEncounterTypeList(Collections.singletonList(new EncounterType(1)));
		cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		
		cd.setEncounterTypeList(Collections.singletonList(new EncounterType(2)));
		cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(0, cohort.size());
	}
	
	/**
	 * @see {@link CodedObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 * 
	 */
	@Ignore
	@Test
	@Verifies(value = "should not return voided patients", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldNotReturnVoidedPatients() throws Exception {
		
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(7));
		
		Patient patient = Context.getPatientService().getPatient(7);
		Context.getPatientService().voidPatient(patient, "testing");
		Context.flushSession();
		
		cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(cohort.contains(7));
	}
}