package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = EvaluatableDataSetDefinition.class)
public class EvaluatableDataSetEvaluator implements DataSetEvaluator {
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		EvaluatableDataSetDefinition dsd = (EvaluatableDataSetDefinition) dataSetDefinition;
		return dsd.evaluate(evalContext);
	}

}
