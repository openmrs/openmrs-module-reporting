package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.definition.SqlQueryDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * 
 */
public class SqlCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
	 * Logger
	 */
	protected final Log log = LogFactory.getLog(getClass());	
	
	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}

	
	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support integer parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportIntegerParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id = :patientId";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientId", new Integer(6));				
		
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();
		cohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}
	
	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support string parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportStringParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id = :patientId";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientId", new String("6"));		
		
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();
		cohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);
		
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support patient parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportPatientParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id = :patientId";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientId", Context.getPatientService().getPatient(6));

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();
		cohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}
	

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support patient parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportIntegerListParameter() throws Exception { 	
		
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientIdList)";
		List<Integer> patientIdList = new ArrayList<Integer>();
		patientIdList.add(new Integer(6));
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientIdList", patientIdList);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();
		cohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		
		
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}
	
	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support patient list parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportPatientListParameter() throws Exception { 		
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientList)";
		List<Patient> patientList = new ArrayList<Patient>();
		patientList.add(Context.getPatientService().getPatient(new Integer(6)));
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientList", patientList);
		
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();
		cohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}
	
	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support cohort parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportCohortParameter() throws Exception { 		
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:cohort)";
		Cohort cohortParam = new Cohort();
		cohortParam.addMember(new Integer(6));		
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("cohort", cohortParam);					
		
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();
		cohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}
	
}
