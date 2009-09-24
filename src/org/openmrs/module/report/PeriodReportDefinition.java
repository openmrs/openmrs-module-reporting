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
public class PeriodReportDefinition extends ReportDefinition {

	public enum PeriodType {
	    ANNUALLY, QUARTERLY, MONTHLY, WEEKLY, DAILY, NONE
	}

	public PeriodReportDefinition() {
		super();
	}
		
}
