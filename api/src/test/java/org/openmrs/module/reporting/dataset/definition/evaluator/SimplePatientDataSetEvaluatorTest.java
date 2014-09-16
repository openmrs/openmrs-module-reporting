package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class SimplePatientDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link SimplePatientDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a SimplePatientDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateASimplePatientDataSetDefinition() throws Exception {
		
		SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
		d.addIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName("Old Identification Number"));
		d.addPatientProperty("patientId");
		d.addPatientProperty("givenName");
		d.addPatientProperty("familyName");
		d.addPatientProperty("gender");
		d.addPatientProperty("age");
		d.addPersonAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Birthplace"));
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(9, result.getRows().size());
		Assert.assertEquals(7, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			Integer patientId = (Integer)row.getColumnValue("patientId");
			Patient p = Context.getPatientService().getPatient(patientId);
			Assert.assertTrue(ObjectUtil.areEqualStr(p.getPatientIdentifier("Old Identification Number"), row.getColumnValue("Old Identification Number")));
			Assert.assertEquals(p.getGivenName(), row.getColumnValue("givenName"));
			Assert.assertEquals(p.getFamilyName(), row.getColumnValue("familyName"));
			Assert.assertEquals(p.getGender(), row.getColumnValue("gender"));
			Assert.assertEquals(p.getAge(), row.getColumnValue("age"));
			Object attVal = p.getAttribute("Birthplace") == null ? null : p.getAttribute("Birthplace").getHydratedObject();
			Assert.assertEquals(attVal, row.getColumnValue("Birthplace"));
		}
	}
}