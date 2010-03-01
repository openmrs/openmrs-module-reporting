package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetWrappingDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * @see DataSetWrappingDataSetDefinition
 * 
 */
@Handler(supports={DataSetWrappingDataSetDefinition.class})
public class DataSetWrappingDataSetEvaluator implements DataSetEvaluator {

	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) {
        DataSetWrappingDataSetDefinition def = (DataSetWrappingDataSetDefinition) dataSetDefinition;
        DataSet data = def.getData();
        if (data instanceof SimpleDataSet) {
            ((SimpleDataSet) data).setDefinition(def);
            ((SimpleDataSet) data).setContext(evalContext);
        }
        return data;
    }
}
