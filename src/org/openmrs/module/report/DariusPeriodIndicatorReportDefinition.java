package org.openmrs.module.report;

import java.util.Date;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition2;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.util.IndicatorUtil;
import org.openmrs.module.report.service.ReportService;


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

	
	public void addDimension(String key, Mapped<CohortDefinitionDimension> dimension) {
		getIndicatorDataSetDefinition().addDimension(key, dimension);
		saveReport();
	}
	
	
	private void saveReport() {
	    Context.getService(ReportService.class).saveReportDefinition(this);
    }


	public void removeDimension(String key) {
		getIndicatorDataSetDefinition().removeDimension(key);
		saveReport();
	}
	
	
	public void addColumn(String key, String displayName, CohortIndicator indicator, Map<String, String> dimensionOptions) {
		getIndicatorDataSetDefinition().addColumn(key, displayName, new Mapped<CohortIndicator>(indicator, IndicatorUtil.periodIndicatorMappings()), dimensionOptions);
		saveDataset();
	}
	
	
	private void saveDataset() {
	    Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(getIndicatorDataSetDefinition());
    }


	public void removeColumn(String key) {
		getIndicatorDataSetDefinition().removeColumn(key);
		saveDataset();
	}
	
	
	public CohortIndicatorDataSetDefinition2 getIndicatorDataSetDefinition() {
		Mapped<CohortIndicatorDataSetDefinition2> ret = (Mapped<CohortIndicatorDataSetDefinition2>) getDataSetDefinitions().get("dataset");
		if (ret != null)
			return ret.getParameterizable();
		else
			return null;
	}


	/**
	 * TODO: move this
	 * 
	 * @param report
	 */
	public static void ensureCorrectlyPersisted(DariusPeriodIndicatorReportDefinition report) {
		report.setupDataSetDefinition();
		if (report.getIndicatorDataSetDefinition().getUuid() == null) {
			Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(report.getIndicatorDataSetDefinition());
			Context.getService(ReportService.class).saveReportDefinition(report);
		}
	}

	
	private void setupDataSetDefinition() {
		// Make sure the report has a data set definition
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
