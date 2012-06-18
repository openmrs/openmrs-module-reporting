package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

@SuppressWarnings("deprecation")
public class DataExportDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see {@link DataExportDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a DataExportDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
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