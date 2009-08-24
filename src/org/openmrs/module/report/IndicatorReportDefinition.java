package org.openmrs.module.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.BaseDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.report.service.ReportService;

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
	public void addCohortIndicator(String columnKey, String displayName, CohortIndicator cohortIndicator) { 

		// @FIXME  Needs to call addIndicator() method when it becomes available
		//getCohortIndicatorDataSetDefinition().addIndicator(key, displayName, cohortIndicator, 
		//		"startDate=${startDate},endDate=${endDate},location=${location}");
		
		CohortIndicatorDataSetDefinition datasetDefinition = getDataSetDefinition();
		
		if (datasetDefinition != null) { 
			log.info("adding indicator with column key " + columnKey + " dataset definition " + datasetDefinition);
			
			// Adding indicator to dataset definition with default parameter mapping
			datasetDefinition.addCohortIndicator(columnKey, cohortIndicator, "startDate=${startDate},endDate=${endDate},location=${location}");
	
			log.info("adding column specification " + columnKey + ", displayName=" + displayName + " cohortIndicator=" + cohortIndicator);
	
			// Adding column specification to dataset 
			datasetDefinition.addColumnSpecification(columnKey, displayName, Number.class, cohortIndicator, null);							
		}
	}
	
	
	/**
	 * Remove the indicator represented by the given indicator key 
	 * from the dataset definition. 
	 * 
	 * @param indicatorKey
	 * 		The indicator key that represents the indicator to be removed.
	 */
	public void removeCohortIndicator(String indicatorKey) { 		
		CohortIndicatorDataSetDefinition datasetDefinition = getDataSetDefinition();
		datasetDefinition.removeCohortIndicator(indicatorKey);
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
		CohortIndicatorDataSetDefinition datasetDefinition = new CohortIndicatorDataSetDefinition();		
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