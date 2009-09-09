package org.openmrs.module.report;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.util.IndicatorUtil;


/**
 * A thin wrapper around a ReportDefinition that gives it startDate, endDate, and location parameters,
 * and a single {@link CohortIndicatorDataSetDefinition} by default.
 * 
 * @see CohortIndicatorDataSetDefinition
 * @see PeriodIndicatorReportUtil
 */
public class PeriodIndicatorReportDefinition extends ReportDefinition {
	
	public PeriodIndicatorReportDefinition() {
		super();

		// add parameters for startDate, endDate, and location
		addParameter(new Parameter("startDate", "Start Date", Date.class));
		addParameter(new Parameter("endDate", "End Date", Date.class));
		addParameter(new Parameter("location", "Location", Location.class));
		
		// a single CohortIndicatorDataSetDefinition
		setupDataSetDefinition();
	}
	
	
	public CohortIndicatorDataSetDefinition getIndicatorDataSetDefinition() {
		Mapped<CohortIndicatorDataSetDefinition> ret = (Mapped<CohortIndicatorDataSetDefinition>) getDataSetDefinitions().get("dataset");
		if (ret != null)
			return ret.getParameterizable();
		else
			return null;
	}

	
	/**
	 * Ensure this report has a data set definition
	 */
	public void setupDataSetDefinition() {
		if (this.getIndicatorDataSetDefinition() == null) {
			CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
			dsd.setName(getName() + " DSD");
			dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
			dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
			dsd.addParameter(new Parameter("location", "Location", Location.class));
			this.addDataSetDefinition("dataset", dsd, IndicatorUtil.periodIndicatorMappings());
		}
    }
	
}
