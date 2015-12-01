package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

@SuppressWarnings("deprecation")
public class DataExportDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link DataExportDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a DataExportDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	@Ignore
	public void evaluate_shouldEvaluateADataExportDataSetDefinition() throws Exception {
		
		DataExportReportObject dataExport = new DataExportReportObject();
		dataExport.setName("Test Name");
		dataExport.setDescription("Test Description");
		dataExport.addSimpleColumn("patientId", "$!{fn.patientId}");
		dataExport.addSimpleColumn("gender", "$!{fn.getPatientAttr('Person', 'gender')}");
		Context.getReportObjectService().saveReportObject(dataExport);
		
		DataExportDataSetDefinition d = new DataExportDataSetDefinition();
		d.setDataExport(dataExport);
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(9, result.getRows().size());
		Assert.assertEquals(2, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			Object patientId = row.getColumnValue("patientId");
			Patient p = Context.getPatientService().getPatient(Integer.valueOf(patientId.toString()));
			Assert.assertEquals(ObjectUtil.nvlStr(p.getGender(), ""), ObjectUtil.nvlStr(row.getColumnValue("gender"), ""));
		}
	}
}