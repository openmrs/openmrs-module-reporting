package org.openmrs.module.reporting.report;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.PeriodCohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.util.OpenmrsUtil;


/**
 * A thin wrapper around a ReportDefinition that gives it startDate, endDate, and location parameters,
 * and a single {@link CohortIndicatorDataSetDefinition} by default.
 * 
 * @see CohortIndicatorDataSetDefinition
 * @see PeriodIndicatorReportUtil
 */
public class PeriodIndicatorReportDefinition extends ReportDefinition {
	
	public static final String DEFAULT_DATASET_KEY = "defaultDataSet";
	
	public PeriodIndicatorReportDefinition() {
		super();

		// add parameters for startDate, endDate, and location
		addParameter(ReportingConstants.START_DATE_PARAMETER);
		addParameter(ReportingConstants.END_DATE_PARAMETER);
		addParameter(ReportingConstants.LOCATION_PARAMETER);
		
		// a single CohortIndicatorDataSetDefinition
		setupDataSetDefinition();
	}
	
	
	/**
	 * Get the indicator dataset definition from the report.  There's only one of these
	 * dataset definitions, so we store it in the dataset definition map with a default
	 * key.
	 * 
	 * @return
	 */
	public CohortIndicatorDataSetDefinition getIndicatorDataSetDefinition() {
		Mapped<CohortIndicatorDataSetDefinition> mappedDataSetDefinition = 
			(Mapped<CohortIndicatorDataSetDefinition>) getDataSetDefinitions().get(DEFAULT_DATASET_KEY);
		if (mappedDataSetDefinition != null)
			return mappedDataSetDefinition.getParameterizable();
		else
			return null;
	}

	/**
	 * Add a period cohort indicator to the report definition with no dimension categories.
	 * 
	 * @param periodCohortIndicator
	 */
	public void addIndicator(PeriodCohortIndicator indicator) {				
		addIndicator(
				indicator.getUuid(),	// need a unique value here
				indicator.getName(),
				indicator,	
				new HashMap<String,String>());
	}
	
	/**
	 * Add a period cohort indicator to the report definition with no dimension categories.
	 * 
	 * @param periodCohortIndicator
	 */
	public void addIndicator(String uniqueName, String displayName, PeriodCohortIndicator indicator) {				
		addIndicator(
				uniqueName,	
				displayName,
				indicator,	
				new HashMap<String,String>());
	}	
	

	/**
	 * Add a period cohort indicator to the report definition with dimension categories.
	 * 
	 * @param periodCohortIndicator
	 */
	public void addIndicator(PeriodCohortIndicator indicator, String dimensionCategories) { 		
		addIndicator(
				indicator.getUuid(), 	// need a unique value here
				indicator.getName(),
				indicator,	
				dimensionCategories
		);
	}	
	
	/**
	 * Add a period cohort indicator to the report definition with dimension categories.
	 * 
	 * @param periodCohortIndicator
	 */
	public void addIndicator(String uniqueName, String displayName, PeriodCohortIndicator indicator, String dimensionCategories) { 		
		addIndicator(
				uniqueName, 
				displayName,
				indicator,	
				OpenmrsUtil.parseParameterList(dimensionCategories)				
		);
	}	
	
	
	/**
	 * Add a period cohort indicator to the report definition with dimension cateogies.
	 * 
	 * @param periodCohortIndicator
	 */
	public void addIndicator(PeriodCohortIndicator indicator, Map<String,String> dimensionCategories) { 		
		addIndicator(
				indicator.getName(), 
				indicator.getName(),
				indicator,	
				dimensionCategories				
		);
	}
	
	/**
	 * Add a period cohort indicator to the report definition with no dimension options
	 * 
	 * @param periodCohortIndicator
	 */
	public void addIndicator(String key, String displayName, PeriodCohortIndicator indicator, Map<String,String> dimensionOptions) { 		
		getIndicatorDataSetDefinition().addColumn(
				key,
				displayName,
				new Mapped<PeriodCohortIndicator>(indicator, IndicatorUtil.getDefaultParameterMappings()),		
				dimensionOptions);		
	}
	
	
	/**
	 * Add dimensions to a period indicator report definition.  This also adds the default 
	 * parameters to the dimension.
	 * 
	 * @param dimensionKey
	 * @param dimension
	 */
	public void addDimension(String dimensionKey, CohortDefinitionDimension dimension) { 	
		dimension.addParameters(IndicatorUtil.getDefaultParameters());
		addDimension(dimensionKey, dimension, IndicatorUtil.getDefaultParameterMappings());
	}

	
	/**
	 * Add dimension to a period indicator report definition where the cohort definition dimension 
	 * needs to be mapped to report parameters.  
	 * 
	 * @param dimensionKey
	 * @param dimension
	 * @param parameterMappings
	 */
	public void addDimension(String dimensionKey, CohortDefinitionDimension dimension, Map<String,Object> parameterMappings) { 		
		getIndicatorDataSetDefinition().addDimension(dimensionKey, dimension, parameterMappings);
	}
	
	
	/**
	 * Ensure this report has a data set definition
	 */
	public void setupDataSetDefinition() {
		if (this.getIndicatorDataSetDefinition() == null) {
			// Create new dataset definition 
			CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
			dataSetDefinition.setName(getName() + " Data Set");
			dataSetDefinition.addParameter(ReportingConstants.START_DATE_PARAMETER);
			dataSetDefinition.addParameter(ReportingConstants.END_DATE_PARAMETER);
			dataSetDefinition.addParameter(ReportingConstants.LOCATION_PARAMETER);
			
			// Add dataset definition to report definition
			addDataSetDefinition(DEFAULT_DATASET_KEY, dataSetDefinition, IndicatorUtil.getDefaultParameterMappings());
		}
    }
	
}
