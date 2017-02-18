package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class SqlCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

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
	@Verifies(value = "should support integer parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportIntegerParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id = :patientId";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientId", new Integer(6));				
		
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
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
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
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
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}
	

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support integer list parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportIntegerListParameter() throws Exception { 	
		
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientIdList)";
		List<Integer> patientIdList = new ArrayList<Integer>();
		patientIdList.add(new Integer(6));
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientIdList", patientIdList);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		
		
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support integer list parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportIntegerSetParameter() throws Exception {

		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientIdList)";
		Set<Integer> patientIdList = new HashSet<Integer>();
		patientIdList.add(new Integer(6));
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientIdList", patientIdList);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support integer list parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportEmptyIntegerListParameter() throws Exception {

		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientIdList)";
		List<Integer> patientIdList = new ArrayList<Integer>();
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientIdList", patientIdList);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(0, cohort.size());
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
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support patient list parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportPatientSetParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientList)";
		Set<Patient> patientList = new HashSet<Patient>();
		patientList.add(Context.getPatientService().getPatient(new Integer(6)));
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientList", patientList);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should support patient list parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSupportEmptyPatientListParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM patient WHERE patient_id IN (:patientList)";
		List<Patient> patientList = new ArrayList<Patient>();
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("patientList", patientList);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(0, cohort.size());
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
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(6));
	}


	/**
     * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     * 
     */
    @Test
    @Verifies(value = "should support date parameter", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldSupportDateParameter() throws Exception {
		String sqlQuery = "SELECT distinct patient_id FROM encounter WHERE encounter_datetime < :date";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("date", new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-18"));
		
		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);		

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
    }

   /**
     * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)}
     */
    @Test(expected = EvaluationException.class)
    @Verifies(value = "should protect SQL Query Against database modifications", method = "evaluate(CohortDefinition , EvaluationContext)")
    public void shouldProtectSqlQueryAgainstDatabaseModifications() throws EvaluationException {
        String query = "update person set gender='F'";
        SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(query);
        EvaluationContext evaluationContext = new EvaluationContext();
        Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);
    }

	/**
	 * @see {@link SqlCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 *
	 */
	@Test
	@Verifies(value = "should evaluate different results for the same query with different parameters", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateDifferentResultsForTheSameQueryWithDifferentParameters() throws Exception {

		SqlCohortDefinition cd = new SqlCohortDefinition("SELECT distinct patient_id FROM encounter WHERE encounter_datetime >= :startParam and encounter_datetime <= :endParam");
		cd.addParameter(new Parameter("startParam", "startParam", Date.class));
		cd.addParameter(new Parameter("endParam", "endParam", Date.class));

		CohortIndicator i1 = CohortIndicator.newCountIndicator("num", new Mapped<CohortDefinition>(cd,
				ParameterizableUtil.createParameterMappings("startParam=${startDate},endParam=${endDate}")), null);
		i1.addParameter(new Parameter("startDate", "Start date", Date.class));
		i1.addParameter(new Parameter("endDate", "End date", Date.class));

		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addParameter(new Parameter("startDate", "Start date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End date", Date.class));

		dsd.addColumn("1", "Num in period", new Mapped(i1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		CohortIndicator i2 = CohortIndicator.newCountIndicator("num", new Mapped<CohortDefinition>(cd,
				ParameterizableUtil.createParameterMappings("startParam=${endDate-1m},endParam=${endDate}")), null);
		i2.addParameter(new Parameter("startDate", "Start date", Date.class));
		i2.addParameter(new Parameter("endDate", "End date", Date.class));

		dsd.addColumn("2", "Num at end of period", new Mapped(i2, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.getDateTime(2009, 8, 19));
		context.addParameterValue("endDate", DateUtil.getDateTime(2009, 10, 20));

		DataSet ds = Context.getService(DataSetDefinitionService.class).evaluate(dsd, context);
		DataSetRow row = ds.iterator().next();

		Assert.assertEquals("5", row.getColumnValue("1").toString());
		Assert.assertEquals("1", row.getColumnValue("2").toString());
	}

	@Test
	public void evaluate_shouldFollowChildLocationsIfIncludeChildLocationsIsTrue() throws Exception {
		String sqlQuery = "SELECT patient_id FROM patient_program WHERE location_id IN (:locationList)";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		Collection<Location> locationList = new ArrayList<Location>();
		locationList.add(Context.getLocationService().getLocation(4));
		parameterValues.put("locationList", locationList);
		parameterValues.put("includeChildLocations", true);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(2, cohort.size());
		Assert.assertTrue(cohort.contains(101));
		Assert.assertTrue(cohort.contains(102));
	}

	@Test
	public void evaluate_shouldNotFollowChildLocationsIfIncludeChildLocationsIsFalse() throws Exception {
		String sqlQuery = "SELECT patient_id FROM patient_program WHERE location_id IN (:locationList)";
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		Collection<Location> locationList = new ArrayList<Location>();
		locationList.add(Context.getLocationService().getLocation(4));
		parameterValues.put("locationList", locationList);
		parameterValues.put("includeChildLocations", false);

		EvaluationContext evaluationContext = new EvaluationContext();
		evaluationContext.setParameterValues(parameterValues);
		SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);

		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(101));
	}
}
