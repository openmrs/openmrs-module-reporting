package org.openmrs.module.dataset.definition.evaluator;

import java.util.Arrays;
import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.SimpleDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;

@Handler(supports={MultiPeriodIndicatorDataSetDefinition.class})
public class MultiPeriodIndicatorDataSetEvaluator implements DataSetEvaluator {
	
	public MultiPeriodIndicatorDataSetEvaluator() { }
	
	/**
	 * @see org.openmrs.module.dataset.definition.evaluator.DataSetEvaluator#evaluate(org.openmrs.module.dataset.definition.DataSetDefinition, org.openmrs.module.evaluation.EvaluationContext)
	 * 
	 * @should evaluate a data set definition
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) {
		if (evalContext == null) {
			evalContext = new EvaluationContext();
		}
		
		MultiPeriodIndicatorDataSetDefinition dsd = (MultiPeriodIndicatorDataSetDefinition) dataSetDefinition;
		SimpleDataSet ret = new SimpleDataSet(dsd, evalContext);
		for (Iteration iter : dsd.getIterations()) {
			EvaluationContext ec = EvaluationContext.clone(evalContext);
			ec.addParameterValue("startDate", iter.getStartDate());
			ec.addParameterValue("endDate", iter.getEndDate());
			ec.addParameterValue("location", iter.getLocation());
			MapDataSet<?> ds = (MapDataSet<?>) Context.getService(DataSetDefinitionService.class).evaluate(dsd.getBaseDefinition(), ec);
			copyIntoDataSet(ret, ds, dsd, ec);
		}
		
		return ret;
	}
	
	private List<String> keysToCopy = Arrays.asList(new String[] { "startDate", "endDate", "location" });

	private void copyIntoDataSet(SimpleDataSet ret, MapDataSet<?> ds, MultiPeriodIndicatorDataSetDefinition definition, EvaluationContext ec) {
	    DataSetRow<Object> row = new DataSetRow<Object>();
	    for (DataSetColumn column : definition.getColumns()) {
	    	if (keysToCopy.contains(column.getColumnKey())) {
	    		row.addColumnValue(column, ec.getParameterValue(column.getColumnKey()));
	    	} else {
	    		row.addColumnValue(column, ds.getData().getColumnValue(column));
	    	}
	    }
    	ret.addRow(row);
    }
	
}
