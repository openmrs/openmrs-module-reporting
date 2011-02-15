package org.openmrs.module.reporting.report.definition;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
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
@Localized("reporting.PeriodIndicatorReportDefinition")
public class PeriodIndicatorReportDefinition extends ReportDefinition {
	
	public static final String DEFAULT_DATASET_KEY = "defaultDataSet";
	
	public PeriodIndicatorReportDefinition() {
		super();
		// add parameters for startDate, endDate, and location
		addParameter(ReportingConstants.START_DATE_PARAMETER);
		addParameter(ReportingConstants.END_DATE_PARAMETER);
		addParameter(ReportingConstants.LOCATION_PARAMETER);
	}
	
	
	/**
	 * @return the indicator dataset definition from the report.  There's only one of these
	 * dataset definitions, so we store it in the dataset definition map with a default
	 * key.
	 */
	@SuppressWarnings("unchecked")
	public CohortIndicatorDataSetDefinition getIndicatorDataSetDefinition() {
		Mapped<CohortIndicatorDataSetDefinition> mappedDataSetDefinition = 
			(Mapped<CohortIndicatorDataSetDefinition>) getDataSetDefinitions().get(DEFAULT_DATASET_KEY);
		if (mappedDataSetDefinition != null) {
			return mappedDataSetDefinition.getParameterizable();
		}
		return null;
	}

	/**
	 * Add a period cohort indicator to the report definition with no dimension categories.
	 * @param periodCohortIndicator
	 */
	public void addIndicator(CohortIndicator indicator) {				
		addIndicator(indicator.getUuid(), indicator.getName(), indicator, new HashMap<String,String>());
	}
	
	/**
	 * Add a period cohort indicator to the report definition with no dimension categories.
	 * @param CohortIndicator
	 */
	public void addIndicator(String uniqueName, String displayName, CohortIndicator indicator) {				
		addIndicator(uniqueName, displayName, indicator, new HashMap<String,String>());
	}

	/**
	 * Add a period cohort indicator to the report definition with dimension categories.
	 * @param CohortIndicator
	 */
	public void addIndicator(CohortIndicator indicator, String dimensionCategories) { 		
		addIndicator(indicator.getUuid(), indicator.getName(), indicator, dimensionCategories);
	}	
	
	/**
	 * Add a period cohort indicator to the report definition with dimension categories.
	 * @param CohortIndicator
	 */
	public void addIndicator(String uniqueName, String displayName, CohortIndicator indicator, String dimensionCategories) { 		
		addIndicator(uniqueName, displayName, indicator, OpenmrsUtil.parseParameterList(dimensionCategories));
	}	
	
	/**
	 * Add a period cohort indicator to the report definition with dimension cateogies.
	 * @param CohortIndicator
	 */
	public void addIndicator(CohortIndicator indicator, Map<String,String> dimensionCategories) { 		
		addIndicator(indicator.getName(), indicator.getName(), indicator, dimensionCategories);
	}
	
	/**
	 * Add a period cohort indicator to the report definition with no dimension options
	 * @param CohortIndicator
	 */
	public void addIndicator(String key, String displayName, CohortIndicator indicator, Map<String,String> dimensionOptions) { 
		Mapped<CohortIndicator> m = new Mapped<CohortIndicator>(indicator, IndicatorUtil.getDefaultParameterMappings());
		getIndicatorDataSetDefinition().addColumn(key, displayName, m, dimensionOptions);		
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
