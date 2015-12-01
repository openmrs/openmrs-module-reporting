package org.openmrs.module.reporting.query.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PatientPersonQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientPersonQueryEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PatientPersonQueryEvaluator#evaluate(PersonQuery,EvaluationContext)
	 * @verifies return all of the person ids for all patients in the defined patient query
	 */
	@Test
	public void evaluate_shouldReturnAllOfThePersonIdsForAllPatientsInTheDefinedPatientQuery() throws Exception {		
		EvaluationContext context = new EvaluationContext();
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setMaleIncluded(true);
		PatientPersonQuery q = new PatientPersonQuery(males);
		PersonQueryResult r = Context.getService(PersonQueryService.class).evaluate(q, context);
		Assert.assertEquals(3, r.getSize());
		Assert.assertTrue(r.getMemberIds().contains(2));
		Assert.assertTrue(r.getMemberIds().contains(6));
		Assert.assertTrue(r.getMemberIds().contains(21));
	}
}