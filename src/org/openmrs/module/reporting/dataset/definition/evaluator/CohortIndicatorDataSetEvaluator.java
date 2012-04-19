package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.indicator.service.IndicatorService;

/**
 * DataSetDefinition containing a Map of CohortIndicators
 */
@Handler(supports={CohortIndicatorDataSetDefinition.class})
public class CohortIndicatorDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Default Constructor
	 */
	public CohortIndicatorDataSetEvaluator() { }
	
	/**
	 * @throws EvaluationException 
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate a CohortIndicatorDataSetDefinition
	 */
	public MapDataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		CohortIndicatorDataSetDefinition dsd = (CohortIndicatorDataSetDefinition) dataSetDefinition;

		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		IndicatorService is = Context.getService(IndicatorService.class);
		DimensionService ds = Context.getService(DimensionService.class);
		
		MapDataSet ret = new MapDataSet(dataSetDefinition, context);
		for (DataSetColumn dsc : dsd.getColumns()) {
			ret.getMetaData().addColumn(dsc);
		}
		
		// evaluate all dimension options
		Map<String, Map<String, Cohort>> dimensionCalculationCache = new HashMap<String, Map<String, Cohort>>();
		for (Map.Entry<String, Mapped<CohortDefinitionDimension>> e : dsd.getDimensions().entrySet()) {
			String dimensionKey = e.getKey();
			try {
				CohortDimensionResult dim = (CohortDimensionResult)ds.evaluate(e.getValue(), context);
				dimensionCalculationCache.put(dimensionKey, dim.getOptionCohorts());
			} catch (Exception ex) {
				throw new EvaluationException("dimension " + dimensionKey, ex);
			}
		}
		
		// evaluate unique indicators
		Map<Mapped<? extends CohortIndicator>, CohortIndicatorResult> indicatorCalculationCache = new HashMap<Mapped<? extends CohortIndicator>, CohortIndicatorResult>();
		for (DataSetColumn c : dsd.getColumns()) {
			CohortIndicatorAndDimensionColumn col = (CohortIndicatorAndDimensionColumn) c;
			if (!indicatorCalculationCache.containsKey(col.getIndicator())) {
				try {
					CohortIndicatorResult result = (CohortIndicatorResult) is.evaluate(col.getIndicator(), context);
					log.debug("Caching indicator: " + col.getIndicator());
					indicatorCalculationCache.put(col.getIndicator(), result);
				} catch (Exception ex) {
					throw new EvaluationException("indicator for column " + col.getLabel() + " (" + col.getName() + ")", ex);
				}
			}
		}
		
		// Populate Data Set columns with Indicator and Dimension Results as defined
		for (DataSetColumn c : dsd.getColumns()) {
			CohortIndicatorAndDimensionColumn col = (CohortIndicatorAndDimensionColumn) c;
			// get this indicator result from the cache
			CohortIndicatorResult result = indicatorCalculationCache.get(col.getIndicator());
			// get its value taking dimensions into account
			CohortIndicatorAndDimensionResult resultWithDimensions = new CohortIndicatorAndDimensionResult(result, context);

			if (col.getDimensionOptions() != null) {
				for (Map.Entry<String, String> e : col.getDimensionOptions().entrySet()) {
					log.debug("looking up dimension: " + e.getKey() + " = " + e.getValue());
					CohortDefinitionDimension dimension = dsd.getDimension(e.getKey()).getParameterizable();
					Cohort dimensionCohort = dimensionCalculationCache.get(e.getKey()).get(e.getValue());
					resultWithDimensions.addDimensionResult(dimension, dimensionCohort);
				}
			}
			ret.addData(col, resultWithDimensions);
		}
		
		return ret;
	}
}
