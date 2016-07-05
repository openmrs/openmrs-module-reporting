package org.openmrs.module.reporting.dataset.definition.evaluator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.Iterator;

import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluatableDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	DataSetDefinitionService service;

	@Test
	public void evaluate() throws Exception {
		EvaluatableDataSetDefinition dsd = new TestEvaluatableDataSetDefinition();
		DataSet dataSet = service.evaluate(dsd, new EvaluationContext());
		assertThat(dataSet.getDefinition(), Is.<DataSetDefinition>is(dsd));

		Iterator<DataSetRow> iter = dataSet.iterator();
		DataSetRow row = iter.next();
		assertThat(row.getColumnValue("one"), Is.<Object>is(1));
		assertFalse(iter.hasNext());
	}
	
}