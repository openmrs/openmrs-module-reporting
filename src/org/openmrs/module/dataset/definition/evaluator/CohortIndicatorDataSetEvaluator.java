/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dataset.definition.evaluator;

import java.util.LinkedHashMap;
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
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.module.indicator.service.IndicatorService;

/**
 * The logic that evaluates a {@link CohortIndicatorDataSetDefinition} 
 * and produces a {@link MapDataSet<IndicatorResult>}
 * 
 * @see CohortIndicatorDataSetDefinition
 * @see MapDataSet
 * @see IndicatorResult
 */
@Handler(supports={CohortIndicatorDataSetDefinition.class})
public class CohortIndicatorDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public CohortIndicatorDataSetEvaluator() { }

	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		log.debug("Evaluating DSD: " + dataSetDefinition + "(" + context.getParameterValues() + ")");
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		IndicatorService is = Context.getService(IndicatorService.class);
		
		MapDataSet<IndicatorResult<?>> data = new MapDataSet<IndicatorResult<?>>(dataSetDefinition, context);
		data.setName(dataSetDefinition.getName());
		
		CohortIndicatorDataSetDefinition dsd = (CohortIndicatorDataSetDefinition) dataSetDefinition;
		
		for (DataSetColumn column : dsd.getColumnSpecifications().keySet()) {
			ColumnDefinition specification = dsd.getColumnSpecifications().get(column);
			
			Mapped<CohortIndicator> baseMappedIndicator = dsd.getIndicator(specification.getIndicatorKey());
			
			CohortIndicatorResult result = (CohortIndicatorResult) is.evaluate(baseMappedIndicator, context);
			
		   	// Dimensions
	    	if (specification.getDimensionQuery() != null) {
    	    	String[] split = specification.getDimensionQuery().split(Dimension.DIMENSION_SEPARATOR);
    	    	for (String dimensionSplit : split) {
    	    		String[] dimensionAndOption = dimensionSplit.split(Dimension.OPTION_SEPARATOR);
    	    		Mapped<? extends Dimension> d = dsd.getDimension(dimensionAndOption[0]);
    	    		if (d == null || dimensionAndOption.length != 2) {
    	    			throw new IllegalArgumentException("Error parsing dimensionOption specification: " + dimensionAndOption);
    	    		}
    	    		if (d.getParameterizable() instanceof CohortDimension) {
    	    			EvaluationContext childContext = EvaluationContext.cloneForChild(context, d);
    	    			CohortDimension cd = (CohortDimension) d.getParameterizable();
    	    			Cohort dimensionCohort = evaluateDimension(result.getCohort(), cd, dimensionAndOption[1], childContext);
    	    			result.addCohortDimension(cd, dimensionAndOption[1], dimensionCohort);
    	    		}
    	    		else {
    	    			throw new IllegalArgumentException("Dimension <" + specification.getDimensionQuery() + "> must be a CohortDimension");
    	    		}
    	    	}
    		}
	    	
	    	data.addData(column, result);
		}
		return data;
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
