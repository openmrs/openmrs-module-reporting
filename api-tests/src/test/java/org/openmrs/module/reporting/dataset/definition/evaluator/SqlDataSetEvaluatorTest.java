package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class SqlDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a SQLDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateASQLDataSetDefinition() throws Exception {
		SqlDataSetDefinition d = new SqlDataSetDefinition();
		d.setSqlQuery("select t.patient_id, p.gender, p.birthdate from patient t, person p where t.patient_id = p.person_id and t.patient_id = 2");
		SimpleDataSet result = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(1, result.getRows().size());
		Assert.assertEquals(3, result.getMetaData().getColumnCount());
		DataSetRow firstRow = result.getRows().get(0);
		Assert.assertEquals(2, firstRow.getColumnValue("patient_id"));
		Assert.assertEquals("M", firstRow.getColumnValue("gender"));
		Assert.assertEquals(DateUtil.getDateTime(1975, 4, 8), firstRow.getColumnValue("birthdate"));
	}
	
	/**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a SQLDataSetDefinition with parameters", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateASQLDataSetDefinitionWithParameters() throws Exception {
		SqlDataSetDefinition d = new SqlDataSetDefinition();
		EvaluationContext c = new EvaluationContext(new Date());
		c.addParameterValue("patientId", 21);
		d.setSqlQuery("select t.patient_id, p.gender, p.birthdate from patient t inner join person p on t.patient_id = p.person_id where t.patient_id = :patientId order by patient_id asc");
		SimpleDataSet result = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(d, c);
		Assert.assertEquals(1, result.getRows().size());
		Assert.assertEquals(3, result.getMetaData().getColumnCount());
		DataSetRow firstRow = result.getRows().get(0);
		Assert.assertEquals(21, firstRow.getColumnValue("patient_id"));
		Assert.assertEquals("M", firstRow.getColumnValue("gender"));
		Assert.assertEquals(DateUtil.getDateTime(1959, 6, 8), firstRow.getColumnValue("birthdate"));
	}
	
	/**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a SQLDataSetDefinition with in statement", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateASQLDataSetDefinitionWithInStatement() throws Exception {
		SqlDataSetDefinition d = new SqlDataSetDefinition();
		EvaluationContext c = new EvaluationContext(new Date());
		c.addParameterValue("patientId", Arrays.asList(21, 22));
		d.setSqlQuery("select t.patient_id, p.gender, p.birthdate from patient t inner join person p on t.patient_id = p.person_id where t.patient_id in :patientId order by patient_id desc");
		SimpleDataSet result = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(d, c);
		Assert.assertEquals(2, result.getRows().size());
		Assert.assertEquals(3, result.getMetaData().getColumnCount());
		DataSetRow firstRow = result.getRows().get(0);
		Assert.assertEquals(22, firstRow.getColumnValue("patient_id"));
		Assert.assertEquals("F", firstRow.getColumnValue("gender"));
		Assert.assertEquals(DateUtil.getDateTime(1997, 7, 8), firstRow.getColumnValue("birthdate"));
	}

	/**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test(expected=EvaluationException.class)
    @Verifies(value = "should protect SQL Query Against database modifications", method = "evaluate(DataSetDefinition,EvaluationContext)")
    public void evaluate_shouldProtectSQLQueryAgainstDatabaseModifications() throws EvaluationException {
        SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
        EvaluationContext context = new EvaluationContext(new Date());
        String query = "update person set gender='F'";
        dataSetDefinition.setSqlQuery(query);
        Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context);
    }

    @Test(expected = EvaluationException.class)
	public void buildQuery_shouldThrowAnExceptionIfDuplicateColumnsExist() throws EvaluationException {
		SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
		dataSetDefinition.setSqlQuery("select patient_id, patient_id from patient");
		Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, new EvaluationContext());
	}

	/**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should handle boolean parameters", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldHandleBooleanParameters() throws EvaluationException {
		SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
		String query = "select name from location where retired = :Retired order by name";
		dataSetDefinition.setSqlQuery(query);
		EvaluationContext context = new EvaluationContext(new Date());
		context.addParameterValue("Retired", Boolean.TRUE);

		SimpleDataSet ds = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context);
		Assert.assertEquals(1, ds.getRows().size());

		context.addParameterValue("Retired", Boolean.FALSE);

		ds = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context);
		Assert.assertEquals(4, ds.getRows().size());
	}

	@Test
	public void evaluate_shouldHandleMetadataListParameters() throws Exception {
		SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
		String query = "select encounter_id, encounter_datetime, location_id from encounter where location_id in (:locations)";
		dataSetDefinition.setSqlQuery(query);

		List<Location> locationList = new ArrayList<Location>();
		locationList.add(Context.getLocationService().getLocation(1));
		locationList.add(Context.getLocationService().getLocation(3));

		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("locations", locationList);

		SimpleDataSet ds = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context);
		Assert.assertEquals(2, ds.getRows().size());
	}
}
