package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Ignore
public class MySqlDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	EvaluationService evaluationService;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	/**
	 * @return MS Note: use port 3306 as standard, 5538 for sandbox 5.5 mysql environment
	 */
	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_mirebalais?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		return p;
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

		Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context);
	}

	@Test
	public void evaluate_shouldAllowAliasesWithSpaces() throws Exception {
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select person_id, birthdate as 'Date of Birth' from person");
		Context.getService(EvaluationService.class).evaluateToList(q, new EvaluationContext());
		List<DataSetColumn> columns = Context.getService(EvaluationService.class).getColumns(q);
		Assert.assertEquals("Date of Birth", columns.get(1).getName());
	}

	@Test
	public void evaluate_shouldHandleNulls() throws Exception {
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("location", null);

		// Test 1
		dsd.setSqlQuery("SELECT IFNULL(:location,0)");
		Context.getService(DataSetDefinitionService.class).evaluate(dsd, context);

		// Test 2
		dsd.setSqlQuery("SELECT COALESCE(:location,1)");
		Context.getService(DataSetDefinitionService.class).evaluate(dsd, context);

		// Test 3
		dsd.setSqlQuery("SELECT * FROM location WHERE :location IS NULL");
		Context.getService(DataSetDefinitionService.class).evaluate(dsd, context);
	}

	@Test
	public void evaluate_shouldAllowVariableInQuery() throws Exception {
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select @numThisYear:=(select count(encounter_datetime) from encounter where voided = 0 and year(encounter_datetime) = :year), (@numThisYear-1000) as numMinus1000");
		for (int year=2012; year<=2014; year++) {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("year", year);
			DataSet dataSet = Context.getService(DataSetDefinitionService.class).evaluate(dsd, context);
			DataSetUtil.printDataSet(dataSet, System.out);
		}
	}

	/**
	 * This test fails, demonstrating that it is not currently possible to execute modifications to the DB
	 * due to the use of "executeQuery" in the SqlQueryBuilder.
	 */
	@Test
	public void evaluate_shouldSupportOnTheFlyStoredProcedures() throws Exception {
		SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
		StringBuilder query = new StringBuilder();
		query.append("CREATE PROCEDURE temp_procedure() \n");
		query.append("SELECT uuid(); \n");
		query.append("CALL temp_procedure(); \n");
		query.append("DROP PROCEDURE temp_procedure; ");
		dataSetDefinition.setSqlQuery(query.toString());

		SimpleDataSet ds = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, new EvaluationContext());
		DataSetUtil.printDataSet(ds, System.out);
	}
}
