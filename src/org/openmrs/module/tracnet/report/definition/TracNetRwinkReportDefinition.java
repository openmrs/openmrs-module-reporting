package org.openmrs.module.tracnet.report.definition;

import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.indicator.util.IndicatorUtil;
import org.openmrs.module.report.PeriodIndicatorReportDefinition;
import org.openmrs.module.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.ReportingConstants;


/**
 * 
 */
public class TracNetRwinkReportDefinition extends PeriodIndicatorReportDefinition {
	
	public static final String DEFAULT_DATASET_KEY = "tracNetDataSet";
	
	/**
	 * Contructor - do not call super()
	 */
	public TracNetRwinkReportDefinition() {
		// a single CohortIndicatorDataSetDefinition
		setupDataSetDefinition();
	}
		
	
	/**
	 * Ensure this report has a data set definition
	 */
	public void setupDataSetDefinition() {
		// Create new dataset definition 
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName(getName() + " Data Set");
		dataSetDefinition.addParameter(ReportingConstants.START_DATE_PARAMETER);
		dataSetDefinition.addParameter(ReportingConstants.END_DATE_PARAMETER);
		
		// Add dataset definition to report definition
		addDataSetDefinition(DEFAULT_DATASET_KEY, dataSetDefinition, IndicatorUtil.getDefaultParameterMappings());
    }
	
}
