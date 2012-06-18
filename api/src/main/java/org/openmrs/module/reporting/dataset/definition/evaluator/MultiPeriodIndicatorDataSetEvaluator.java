package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.Arrays;
import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports={MultiPeriodIndicatorDataSetDefinition.class})
public class MultiPeriodIndicatorDataSetEvaluator implements DataSetEvaluator {
	
	public MultiPeriodIndicatorDataSetEvaluator() { }
	
	/**
	 * @throws EvaluationException 
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate a MultiPeriodIndicatorDataSetDefinition
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		if (context == null) {
			context = new EvaluationContext();
		}
		List<String> keysToCopy = Arrays.asList(new String[] { "startDate", "endDate", "location" });
		
		MultiPeriodIndicatorDataSetDefinition dsd = (MultiPeriodIndicatorDataSetDefinition) dataSetDefinition;
		SimpleDataSet ret = new SimpleDataSet(dsd, context);
		
		for (Iteration iter : dsd.getIterations()) {
			EvaluationContext ec = context.shallowCopy();
			ec.addParameterValue("startDate", iter.getStartDate());
			ec.addParameterValue("endDate", iter.getEndDate());
			ec.addParameterValue("location", iter.getLocation());
			MapDataSet ds;
			try {
				ds = (MapDataSet) Context.getService(DataSetDefinitionService.class).evaluate(dsd.getBaseDefinition(), ec);
			} catch (Exception ex) {
				throw new EvaluationException("baseDefinition", ex);
			}
		    DataSetRow row = new DataSetRow();
		    for (DataSetColumn column : dsd.getColumns()) {
		    	if (keysToCopy.contains(column.getName())) {
		    		row.addColumnValue(column, ec.getParameterValue(column.getName()));
		    	} 
		    	else {
		    		row.addColumnValue(column, ds.getData(column));
		    	}
		    }
	    	ret.addRow(row);
		}
		
		return ret;
	}
}
