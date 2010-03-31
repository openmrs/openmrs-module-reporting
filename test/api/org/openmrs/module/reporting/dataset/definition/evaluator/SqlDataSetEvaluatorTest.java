package org.openmrs.module.reporting.dataset.definition.evaluator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class SqlDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a SQLDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateASQLDataSetDefinition() throws Exception {
		SqlDataSetDefinition d = new SqlDataSetDefinition();
		d.setSqlQuery("select t.patient_id, p.gender, p.birthdate from patient t, person p where t.patient_id = p.person_id order by patient_id asc");
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(10, result.getRows().size());
		Assert.assertEquals(3, result.getMetaData().getColumnCount());
		DataSetRow firstRow = result.getRows().get(0);
		Assert.assertEquals(2, firstRow.getColumnValue("patient_id"));
		Assert.assertEquals("M", firstRow.getColumnValue("gender"));
		Assert.assertEquals(DateUtil.getDateTime(1975, 4, 8), firstRow.getColumnValue("birthdate"));
	}
}