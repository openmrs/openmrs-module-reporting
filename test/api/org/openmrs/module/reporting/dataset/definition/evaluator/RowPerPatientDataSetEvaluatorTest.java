package org.openmrs.module.reporting.dataset.definition.evaluator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.definition.person.AgeColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.GenderColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.PersonIdColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RowPerPatientDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	@Test
	public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {
		
		RowPerPatientDataSetDefinition d = new RowPerPatientDataSetDefinition();
		d.addColumnDefinition(new PersonIdColumnDefinition("Person ID"));
		d.addColumnDefinition(new GenderColumnDefinition("Sexe"));
		d.addColumnDefinition(new AgeColumnDefinition("Age"));
		
		EvaluationContext context = new EvaluationContext();
		
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assert.assertEquals("F", dataset.getColumnValue(7, "Sexe"));
		Assert.assertNull(dataset.getColumnValue(501, "Sexe"));
		Assert.assertEquals(9, dataset.getRows().size());
		
		Cohort c = new Cohort("2,6,8");
		context.setBaseCohort(c);
		
		dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assert.assertEquals(3, dataset.getRows().size());
	}
}