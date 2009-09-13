package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition.ColumnDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.CohortIndicatorResult;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.dimension.CohortDimension;
import org.openmrs.module.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.module.indicator.service.IndicatorService;

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
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public MapDataSet<IndicatorResult<CohortIndicator>> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			context.setBaseCohort(Context.getPatientSetService().getAllPatients());
		}
		
		IndicatorService is = Context.getService(IndicatorService.class);
		
		MapDataSet<IndicatorResult<CohortIndicator>> ret = new MapDataSet<IndicatorResult<CohortIndicator>>(dataSetDefinition, context);
		ret.setName(dataSetDefinition.getName());
		
		CohortIndicatorDataSetDefinition dsd = (CohortIndicatorDataSetDefinition) dataSetDefinition;
		
		// evaluate all dimension options
		Map<String, Map<String, Cohort>> dimensionCalculationCache = new HashMap<String, Map<String, Cohort>>();
		for (Map.Entry<String, Mapped<CohortDefinitionDimension>> e : dsd.getDimensions().entrySet()) {
			String dimensionKey = e.getKey();
			EvaluationContext ec = EvaluationContext.cloneForChild(context, e.getValue());
			Map<String, Cohort> eval = evaluateDimension(context.getBaseCohort(), e.getValue().getParameterizable(), ec);
			dimensionCalculationCache.put(dimensionKey, eval);
		}
		
		// evaluate unique indicators
		Map<Mapped<? extends CohortIndicator>, CohortIndicatorResult> indicatorCalculationCache = new HashMap<Mapped<? extends CohortIndicator>, CohortIndicatorResult>();
		for (DataSetColumn c : dsd.getColumns()) {
			ColumnDefinition col = (ColumnDefinition) c;
			if (!indicatorCalculationCache.containsKey(col.getIndicator())) {
				CohortIndicatorResult result = (CohortIndicatorResult) is.evaluate(col.getIndicator(), context);
				log.debug("Caching indicator: " + col.getIndicator() + " -> " + result.getCohortValues().size());
				indicatorCalculationCache.put(col.getIndicator(), result);
			}
		}
		
		// evaluate indicators
		for (DataSetColumn c : dsd.getColumns()) {
			ColumnDefinition col = (ColumnDefinition) c;
			// get this indicator result from the cache
			CohortIndicatorResult result = indicatorCalculationCache.get(col.getIndicator());
			// get its value taking dimensions into account
			CohortIndicatorAndDimensionResult resultWithDimensions = new CohortIndicatorAndDimensionResult(result, context);
			List<Cohort> filters = new ArrayList<Cohort>();
			if (col.getDimensionOptions() != null) {
				for (Map.Entry<String, String> e : col.getDimensionOptions().entrySet()) {
					log.debug("looking up dimension: " + e.getKey() + " = " + e.getValue());
					Cohort temp = dimensionCalculationCache.get(e.getKey()).get(e.getValue());
					resultWithDimensions.applyFilter(e.getKey(), temp);
					filters.add(temp);
				}
			}
			ret.addData(col, resultWithDimensions);
		}
		
		return ret;
	}

	/**
	 * Evaluates a Dimension with the inputCohort as a basis
	 */
	protected Map<String, Cohort> evaluateDimension(Cohort inputCohort, CohortDimension dimension, EvaluationContext context) {
	
		Cohort totalDimensions = new Cohort();
		Map<String, Cohort> cohorts = new LinkedHashMap<String, Cohort>();
		for (String key : dimension.getOptionKeys()) {
			Cohort currentCohort = evaluateDimension(inputCohort, dimension, key, context);
			cohorts.put(key, currentCohort);
			totalDimensions = Cohort.union(totalDimensions, currentCohort);
		}
		cohorts.put(Dimension.UNCLASSIFIED, Cohort.subtract(inputCohort, totalDimensions));
		
		return cohorts;
	}
	
	/**
	 * Evaluates the passed dimension option with the inputCohort as a basis
	 */
	protected Cohort evaluateDimension(Cohort inputCohort, CohortDimension dimension, String option, EvaluationContext context) {
		
		log.debug("Evaluating dimension: " + dimension + "." + option + "(" + context.getParameterValues() + ")");
	
		CohortDefinitionDimension d = (CohortDefinitionDimension) dimension;
		if (Dimension.UNCLASSIFIED.equalsIgnoreCase(option)) {
			Map<String, Cohort> allCohorts = evaluateDimension(inputCohort, dimension, context);
			return allCohorts.get(Dimension.UNCLASSIFIED);
		}
		
		Mapped<CohortDefinition> mappedDef = d.getCohortDefinition(option);
		if (mappedDef == null) {
			throw new IllegalArgumentException("No CohortDefinition dimension option found for option: " + option);
		}
		Cohort found = Context.getService(CohortDefinitionService.class).evaluate(mappedDef, context);
		if (found == null) {
			return new Cohort();
		}
		return Cohort.intersect(inputCohort, found);
	}
	
}
