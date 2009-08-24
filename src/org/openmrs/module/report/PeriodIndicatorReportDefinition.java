package org.openmrs.module.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.BaseDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.report.service.ReportService;

/**
 * This class represents the metadata that describes an period indicator report.
 * A period indicator report has a single dataset definition with an arbitrary
 * number of indicators. The distinguishing characteristic of the period
 * indicator report is that it contains three parameters (startDate, endDate,
 * location) that can be used by the underlying dataset definition or indicator.
 * 
 * A {@link ReportDefinition} will typically be evaluated upon a base
 * {@link Cohort} in the context of an {@link EvaluationContext}. Evaluating a
 * report generally means evaluating all of the {@link DataSetDefinition}s it
 * contains, resulting in a {@link ReportData}.
 * 
 */
public class PeriodIndicatorReportDefinition extends IndicatorReportDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Public constructor
	 */
	public PeriodIndicatorReportDefinition() {
		super();
		if (this.getUuid() == null) {
			this.addParameter(new Parameter("startDate", "Enter a Start Date",
					Date.class, null, true));
			this.addParameter(new Parameter("endDate", "Enter an End Date",
					Date.class, null, true));
			this.addParameter(new Parameter("location", "Choose a Location",
					Location.class, null, true));
		}
	}


}