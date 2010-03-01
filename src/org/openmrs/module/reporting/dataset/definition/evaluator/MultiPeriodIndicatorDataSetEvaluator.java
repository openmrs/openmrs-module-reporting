package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.Arrays;
import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports={MultiPeriodIndicatorDataSetDefinition.class})
public class MultiPeriodIndicatorDataSetEvaluator implements DataSetEvaluator {
	
	public MultiPeriodIndicatorDataSetEvaluator() { }
	
	/**
	 * @see org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator#evaluate(org.openmrs.module.reporting.dataset.definition.DataSetDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * 
	 * @should evaluate a data set definition
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) {
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
			MapDataSet ds = (MapDataSet) Context.getService(DataSetDefinitionService.class).evaluate(dsd.getBaseDefinition(), ec);
			copyIntoDataSet(ret, ds, dsd, ec);
		}
		
		return ret;
	}
	
	private List<String> keysToCopy = Arrays.asList(new String[] { "startDate", "endDate", "location" });

	private void copyIntoDataSet(SimpleDataSet ret, MapDataSet ds, MultiPeriodIndicatorDataSetDefinition definition, EvaluationContext ec) {
	    DataSetRow row = new DataSetRow();
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
