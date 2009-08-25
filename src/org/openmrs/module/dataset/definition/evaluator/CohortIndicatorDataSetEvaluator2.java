package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition2;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition2.ColumnDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.CohortIndicatorResult;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.service.IndicatorService;


@Handler(supports={CohortIndicatorDataSetDefinition2.class})
public class CohortIndicatorDataSetEvaluator2 implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public CohortIndicatorDataSetEvaluator2() { }
	
	public MapDataSet<Number> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) {
		if (evalContext == null) {
			evalContext = new EvaluationContext();
		}
		
		MapDataSet<Number> ret = new MapDataSet<Number>();
		
		CohortIndicatorDataSetDefinition2 dsd = (CohortIndicatorDataSetDefinition2) dataSetDefinition;
		
		// evaluate all dimension options
		Map<String, Map<String, Cohort>> dimensionCalculationCache = new HashMap<String, Map<String, Cohort>>();
		for (Map.Entry<String, Mapped<CohortDefinitionDimension>> e : dsd.getDimensions().entrySet()) {
			String dimensionKey = e.getKey();
			EvaluationContext ec = EvaluationContext.cloneForChild(evalContext, e.getValue());
			for (Map.Entry<String, Mapped<CohortDefinition>> f : e.getValue().getParameterizable().getCohortDefinitions().entrySet()) {
				String optionKey = f.getKey();
				Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(f.getValue(), ec);
				Map<String, Cohort> temp = dimensionCalculationCache.get(dimensionKey);
				if (temp == null) {
					temp = new HashMap<String, Cohort>();
					dimensionCalculationCache.put(dimensionKey, temp);
				}
				log.warn("Caching dimension: " + dimensionKey + " = " + optionKey + " -> " + cohort.size());
				temp.put(optionKey, cohort);
			}
		}
		
		// evaluate unique indicators
		Map<Mapped<CohortIndicator>, CohortIndicatorResult> indicatorCalculationCache = new HashMap<Mapped<CohortIndicator>, CohortIndicatorResult>();
		for (DataSetColumn c : dsd.getColumns()) {
			ColumnDefinition col = (ColumnDefinition) c;
			if (!indicatorCalculationCache.containsKey(col.getIndicator())) {
				CohortIndicatorResult result = (CohortIndicatorResult) Context.getService(IndicatorService.class).evaluate(col.getIndicator(), evalContext);
				log.warn("Caching indicator: " + col.getIndicator() + " -> " + result.getCohortValues().size());
				indicatorCalculationCache.put(col.getIndicator(), result);
			}
		}
		
		// evaluate indicators
		for (DataSetColumn c : dsd.getColumns()) {
			ColumnDefinition col = (ColumnDefinition) c;
			// get this indicator result from the cache
			CohortIndicatorResult result = indicatorCalculationCache.get(col.getIndicator());
			// get its value taking dimensions into account
			List<Cohort> filters = new ArrayList<Cohort>();
			if (col.getDimensionOptions() != null) {
				for (Map.Entry<String, String> e : col.getDimensionOptions().entrySet()) {
					log.warn("looking up dimension: " + e.getKey() + " = " + e.getValue());
					Cohort temp = dimensionCalculationCache.get(e.getKey()).get(e.getValue());
					filters.add(temp);
				}
			}
			Number thisResult = filters.size() > 0 ? result.getValueForSubset(filters.toArray(new Cohort[0])) : result.getValue();
			ret.addData(col, thisResult);
		}
		
		return ret;
	}
	
}
