package org.openmrs.module.report;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;

/**
 * This class represents the metadata that describes an indicator report. 
 * 
 * A {@link ReportDefinition} will typically be evaluated upon a base {@link Cohort} in the context
 * of an {@link EvaluationContext}.  Evaluating a report generally means evaluating all of the
 * {@link DataSetDefinition}s it contains, resulting in a {@link ReportData}.
 * 
 */
public class IndicatorReportDefinition extends ReportDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @param indicator
	 */
	public void addCohortIndicator(String columnKey, String displayName, CohortIndicator cohortIndicator, Map<String, Object> parameterMapping) { 

		log.info("Adding indicator to report: " + columnKey + " " + displayName + " " + parameterMapping);
		CohortIndicatorDataSetDefinition dataSetDefinition = getDataSetDefinition();
		
		if (dataSetDefinition == null) {			
			// Dataset definition should be created under the covers
			dataSetDefinition = new CohortIndicatorDataSetDefinition();
			dataSetDefinition.setName(this.getName() + " Dataset");
			dataSetDefinition.setParameters(this.getParameters());

			// Save the dataset definition explicitly so it has a uuid
			if (dataSetDefinition.getUuid() == null) { 
				dataSetDefinition = (CohortIndicatorDataSetDefinition)
					Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);				
			}			
			// Add dataset definition to the report
			this.addDataSetDefinition(dataSetDefinition.getName(),
					dataSetDefinition, parameterMapping);			
		}
		
		// Adding indicator to dataset definition with default parameter mapping
		log.info("adding indicator with column key " + columnKey + " dataset definition " + dataSetDefinition + " parameter mapping = " + parameterMapping);
		dataSetDefinition.addIndicator(
				columnKey, 
				displayName,
				cohortIndicator, 
				parameterMapping);

		
		// TODO Need to move this functionality to the service layer.
		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);
		
	}
	
	
	/**
	 * Remove the indicator represented by the given indicator key 
	 * from the dataset definition. 
	 * 
	 * @param indicatorKey
	 * 		The indicator key that represents the indicator to be removed.
	 */
	public void removeIndicator(String indicatorKey) { 		
		CohortIndicatorDataSetDefinition dataSetDefinition = getDataSetDefinition();
		dataSetDefinition.removeCohortIndicator(indicatorKey);		
	}

	/**
	 * Remove the column specification represented by the given column key 
	 * from the dataset definition. 
	 * 
	 * @param indicatorKey
	 * 		The indicator key that represents the indicator to be removed.
	 */
	public void removeColumnSpecification(String columnKey) { 		
		CohortIndicatorDataSetDefinition dataSetDefinition = getDataSetDefinition();
		dataSetDefinition.removeColumnSpecification(columnKey);
		// TODO Need to move this functionality to the service layer.
		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);
	}
	
	
	/**
	 * Returns columns from the dataset definition.
	 * 
	 * @return
	 * 		the columns from the dataset definition.
	 */
	public List<DataSetColumn> getColumns() { 
		CohortIndicatorDataSetDefinition datasetDefinition = 
			getDataSetDefinition();
		
		if (datasetDefinition != null) 
			return datasetDefinition.getColumns();			
		
		return new LinkedList<DataSetColumn>();
	}
	
	/**
	 * Get cohort indicators from the dataset definition.
	 * 
	 * @return 	a map of string to cohort indicator
	 */
	public Map<String, CohortIndicator> getCohortIndicators() { 		
		Map<String, CohortIndicator> cohortIndicators = new HashMap<String, CohortIndicator>();
		
		CohortIndicatorDataSetDefinition dataSetDefinition = getDataSetDefinition();
		if ( dataSetDefinition != null) { 
			Map<String, Mapped<CohortIndicator>> mappedIndicators = dataSetDefinition.getIndicators();		
			for(String key : mappedIndicators.keySet()) { 
				cohortIndicators.put(key, mappedIndicators.get(key).getParameterizable());
			}
		}
		return cohortIndicators;		
	}

	
	/** 
	 * Get all indicators in the report. 
	 * 
	 * @return
	 * 		A list of indicators.
	 */
	public List<Indicator> getIndicators() { 
		// Code required just to get indicators from the report definition
		List<Indicator> indicators = new LinkedList<Indicator>();		
		CohortIndicatorDataSetDefinition dataSetDefinition = getDataSetDefinition();		
		if (dataSetDefinition != null) { 
			Map<String, Mapped<CohortIndicator>> mappedIndicators = 
				dataSetDefinition.getIndicators();
			if (mappedIndicators != null) { 
				for (Mapped<CohortIndicator> indicator : mappedIndicators.values()) { 
					indicators.add(indicator.getParameterizable());
				}
			}
		}
		return indicators;
	}	
	
	

	/**
	 * Gets the dataset definition that contains all indicators for the report definition.
	 * 
	 * @return
	 * 		A dataset definition containing all indicators
	 */
	public CohortIndicatorDataSetDefinition getDataSetDefinition() { 
		CohortIndicatorDataSetDefinition datasetDefinition = null;		
		Map<String, Mapped<? extends DataSetDefinition>> datasetDefinitions = getDataSetDefinitions();
		if (datasetDefinitions != null && !datasetDefinitions.isEmpty()) { 						
			Collection<Mapped<? extends DataSetDefinition>> mappedList = datasetDefinitions.values();
			if (mappedList != null && !mappedList.isEmpty()) { 				
				Mapped<? extends DataSetDefinition> mappedDatasetDefinition = mappedList.iterator().next();			
				if (mappedDatasetDefinition.getParameterizable() instanceof CohortIndicatorDataSetDefinition) { 
					datasetDefinition = 
						(CohortIndicatorDataSetDefinition) mappedDatasetDefinition.getParameterizable();
				}
			}
		}									
		return datasetDefinition;
	}	

	
	
}