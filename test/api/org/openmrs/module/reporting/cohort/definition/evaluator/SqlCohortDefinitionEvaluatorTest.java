package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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
	@Verifies(value = "should return some patients", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnSomePatients() throws Exception {
		// Bind parameter values 
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientId", new String("6"));		
				
		// Create new evaluation context
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);

		// Create new SQL cohort query
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id = :patientId";
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
		sqlCohortDefinition.setQueryDefinition(new SqlQueryDefinition(sqlQuery));
		
		// Evaludate the cohort definition
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(
				sqlCohortDefinition, evaluationContext);
		
		// Assert test cases
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}

}
