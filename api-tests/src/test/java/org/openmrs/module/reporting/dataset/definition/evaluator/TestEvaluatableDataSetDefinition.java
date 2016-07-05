package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Defined in its own file because defining it as an inner class in {@link EvaluatableDataSetEvaluatorTest} throws an
 * internal reporting exception
 */
public class TestEvaluatableDataSetDefinition extends EvaluatableDataSetDefinition {
	
	@Override
	public DataSet evaluate(EvaluationContext evalContext) {
		SimpleDataSet ds = new SimpleDataSet(this, evalContext);
		ds.addColumnValue(0, new DataSetColumn("one", "One", Integer.class), 1);
		return ds;
	}

}
