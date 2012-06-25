package org.openmrs.module.reporting.cohort.definition.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.SqlCohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * 
 */
public class BaseCohortDefinitionServiceTest extends BaseModuleContextSensitiveTest {

	/**
	 * Logger
	 */
	protected final Log log = LogFactory.getLog(getClass());	
	
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
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should save sql cohort definition", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSaveSqlCohortDefinition() throws Exception {		
		String name = "new name";
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id = :patientId";
		
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(sqlQuery);
		sqlCohortDefinition.setName(name);
		
		 sqlCohortDefinition = 
			Context.getService(CohortDefinitionService.class).saveDefinition(sqlCohortDefinition);

		CohortDefinition savedCohortDefinition = 
			Context.getService(CohortDefinitionService.class).getDefinitionByUuid(sqlCohortDefinition.getUuid());

		SqlCohortDefinition savedSqlCohortDefinition = 
			(SqlCohortDefinition) savedCohortDefinition;
		
		log.warn("parameters = " + sqlCohortDefinition.getParameters());
		
		Assert.assertNotNull(savedCohortDefinition);
		Assert.assertEquals(savedCohortDefinition.getName(), name);
		Assert.assertEquals(savedCohortDefinition.getClass(), SqlCohortDefinition.class);		
		Assert.assertEquals(savedSqlCohortDefinition.getQuery(), sqlQuery);
		
	}

}
