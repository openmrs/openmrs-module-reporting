package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition.SimpleIndicatorColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;


@Handler(supports = { SimpleIndicatorDataSetDefinition.class })
public class SimpleIndicatorDataSetEvaluator implements DataSetEvaluator{
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public SimpleIndicatorDataSetEvaluator(){};
	
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		SimpleDataSet ret = new SimpleDataSet(dataSetDefinition, context);
		SimpleIndicatorDataSetDefinition dsd = (SimpleIndicatorDataSetDefinition) dataSetDefinition;
		for (DataSetColumn dsc : dsd.getColumns()) {
			ret.getMetaData().addColumn(dsc);
		}
		IndicatorService is = Context.getService(IndicatorService.class);
		
		Map<Mapped<? extends Indicator>, SimpleIndicatorResult> indicatorCalculationCache = new HashMap<Mapped<? extends Indicator>, SimpleIndicatorResult>();
		for (DataSetColumn c : dsd.getColumns()) {
			SimpleIndicatorColumn col = (SimpleIndicatorColumn) c;
			if (!indicatorCalculationCache.containsKey(col.getIndicator())) {
				try {
					SimpleIndicatorResult result = (SimpleIndicatorResult) is.evaluate(col.getIndicator(), context);
					log.debug("Caching indicator: " + col.getIndicator());
					indicatorCalculationCache.put(col.getIndicator(), result);
				} catch (Exception ex) {
					throw new EvaluationException("indicator for column " + col.getLabel() + " (" + col.getName() + ")", ex);
				}
			}
		}
		
		
		for (DataSetColumn c : dsd.getColumns()) {
			SimpleIndicatorColumn col = (SimpleIndicatorColumn) c;
			SimpleIndicatorResult result = indicatorCalculationCache.get(col.getIndicator());
			ret.addColumnValue(0, c, result.getValue()); // this returns a single row
		}
		
		
		return ret;
	}
	
}
