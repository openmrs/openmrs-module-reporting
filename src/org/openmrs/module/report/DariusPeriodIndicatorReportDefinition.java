package org.openmrs.module.report;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition2;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.util.IndicatorUtil;


public class DariusPeriodIndicatorReportDefinition extends ReportDefinition {
	
	public DariusPeriodIndicatorReportDefinition() {
		super();

		// add parameters for startDate, endDate, and location
		addParameter(new Parameter("startDate", "Start Date", Date.class));
		addParameter(new Parameter("endDate", "End Date", Date.class));
		addParameter(new Parameter("location", "Location", Location.class));
		
		// a single CohortIndicatorDataSetDefinition
		setupDataSetDefinition();
	}
	
	
	public CohortIndicatorDataSetDefinition2 getIndicatorDataSetDefinition() {
		Mapped<CohortIndicatorDataSetDefinition2> ret = (Mapped<CohortIndicatorDataSetDefinition2>) getDataSetDefinitions().get("dataset");
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
			CohortIndicatorDataSetDefinition2 dsd = new CohortIndicatorDataSetDefinition2();
			dsd.setName("Internal Period Indicator Report DSD");
			dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
			dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
			dsd.addParameter(new Parameter("location", "Location", Location.class));
			this.addDataSetDefinition("dataset", dsd, IndicatorUtil.periodIndicatorMappings());
		}
    }
	
}
