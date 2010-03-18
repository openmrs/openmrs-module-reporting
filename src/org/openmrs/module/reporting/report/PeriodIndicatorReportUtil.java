package org.openmrs.module.reporting.report;

import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;


/**
 * Utility methods that allow you to modify a PeriodIndicatorReport, and immediately save
 * those changes to the database 
 */
public class PeriodIndicatorReportUtil {

	// ==== PUBLIC ====
	
	public static void addDimension(PeriodIndicatorReportDefinition def, String key, Mapped<CohortDefinitionDimension> dimension) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().addDimension(key, dimension);
		saveDataSetDefinition(def);
	}
	
	public static void removeDimension(PeriodIndicatorReportDefinition def, String key) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().removeDimension(key);
		saveReportDefinition(def);
	}
	
	
	public static void addColumn(PeriodIndicatorReportDefinition def, String key, String displayName, CohortIndicator indicator, Map<String, String> dimensionOptions) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().addColumn(key, displayName, new Mapped<CohortIndicator>(indicator, IndicatorUtil.getDefaultParameterMappings()), dimensionOptions);
		saveDataSetDefinition(def);
	}
	
	
	public static void removeColumn(PeriodIndicatorReportDefinition def, String key) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().removeColumn(key);
		saveDataSetDefinition(def);
	}


	public static void ensureDataSetDefinition(PeriodIndicatorReportDefinition def) {
	    if (def.getIndicatorDataSetDefinition() == null) {
	    	def.setupDataSetDefinition();
	    	saveDataSetDefinition(def);
	    	saveReportDefinition(def);
	    }
    }

	// ==== PRIVATE ====
	
	private static void saveReportDefinition(ReportDefinition def) {
	    Context.getService(ReportDefinitionService.class).saveDefinition(def);
    }

	private static void saveDataSetDefinition(PeriodIndicatorReportDefinition def) {
		Context.getService(DataSetDefinitionService.class).saveDefinition(def.getIndicatorDataSetDefinition());
    }

}
