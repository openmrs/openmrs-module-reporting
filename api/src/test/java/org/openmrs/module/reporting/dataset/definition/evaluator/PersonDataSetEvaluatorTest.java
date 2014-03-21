package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	
	@Test
	public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {
		
		/*
		RowPerPersonDataSetDefinition d = new RowPerPersonDataSetDefinition();
		d.addColumnDefinition(new PersonIdColumnDefinition("Person ID"));
		d.addColumnDefinition(new GenderColumnDefinition("Sexe"));
		d.addColumnDefinition(new AgeColumnDefinition("Age"));
		
		EvaluationContext context = new EvaluationContext();
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assert.assertEquals("F", dataset.getColumnValue(7, "Sexe"));
		Assert.assertEquals(13, dataset.getRows().size());
		
		PersonEvaluationContext pec = new PersonEvaluationContext();
		PersonQueryResult personQuery = new PersonQueryResult();
		personQuery.add(2,6,8,501);
		pec.setBasePersons(personQuery);
		
		dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, pec);
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assert.assertNull(dataset.getColumnValue(7, "Sexe"));
		Assert.assertEquals(4, dataset.getRows().size());
		*/
	}
}