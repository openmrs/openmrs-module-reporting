package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;

/**
 * Evaluates a CohortIndicatorAndDimensionDataSetDefinition and produces a DataSet
 */
@Handler(supports={CohortIndicatorAndDimensionDataSetDefinition.class})
public class CohortIndicatorAndDimensionDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Default Constructor
	 */
	public CohortIndicatorAndDimensionDataSetEvaluator() { }
	
	/**
	 * @throws EvaluationException 
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate a CohortIndicatorDataSetDefinition
	 */
	public MapDataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		CohortIndicatorAndDimensionDataSetDefinition dsd = (CohortIndicatorAndDimensionDataSetDefinition) dataSetDefinition;
		MapDataSet ret = new MapDataSet(dataSetDefinition, context);

		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		IndicatorService is = Context.getService(IndicatorService.class);
		DimensionService ds = Context.getService(DimensionService.class);

		for (CohortIndicatorAndDimensionSpecification spec : dsd.getSpecifications()) {

			// Get all dimension combinations to include
			List<String> combinations = IndicatorUtil.compileColumnDimensionOptions(spec.getDimensionOptions());
			combinations.add(0, null); // Add in the "no dimension" case at the start
			
			for (String combination : combinations) {
				
				// First evaluate the indicator and create the result object
				CohortIndicatorResult result;
				try {
					result = (CohortIndicatorResult) is.evaluate(spec.getIndicator(), context);
				} catch (Exception ex) {
					throw new EvaluationException("indicator " + spec.getLabel() + " (" + spec.getIndicatorNumber() + ")");
				}
				log.debug("Evaluated Indicator: " + spec.getLabel() + " = " + result.getValue());
				CohortIndicatorAndDimensionResult resultWithDimensions = new CohortIndicatorAndDimensionResult(result, context);
				
				// Set up the basic column definition
				DataSetColumn column = new DataSetColumn(spec.getIndicatorNumber(), spec.getLabel(), Object.class);	

				if (combination != null) {
					for (String option : combination.split(",")) {
						String[] dimOpt = option.split("=");
						column.setName(column.getName() + "." + option);
						column.setLabel(column.getLabel() + (column.getLabel().equals(spec.getLabel()) ? " (" : ", ") + dimOpt[0] + " - " + dimOpt[1]);
	
						Mapped<CohortDefinitionDimension> dimension = dsd.getDimension(dimOpt[0]);
						try { 
							CohortDimensionResult dimensionResult = (CohortDimensionResult)ds.evaluate(dimension, context);
							Cohort dimensionCohort = dimensionResult.getCohort(dimOpt[1]);
							
							resultWithDimensions.addDimensionResult(dimension.getParameterizable(), dimensionCohort);
						} catch (Exception ex) {
							throw new EvaluationException("dimension " + option, ex);
						}
					}
					column.setLabel(column.getLabel() + ")");
				}

				ret.addData(column, resultWithDimensions);
			}
		}
		return ret;
	}
}
