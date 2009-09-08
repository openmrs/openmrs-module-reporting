package org.openmrs.module.report;

import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.util.IndicatorUtil;
import org.openmrs.module.report.service.ReportService;


/**
 * Utility methods that allow you to modify a PeriodIndicatorReport, and immediately save
 * those changes to the database 
 */
public class DariusPeriodIndicatorReportUtil {

	// ==== PUBLIC ====
	
	public static void addDimension(DariusPeriodIndicatorReportDefinition def, String key, Mapped<CohortDefinitionDimension> dimension) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().addDimension(key, dimension);
		saveDataSetDefinition(def);
	}
	
	public static void removeDimension(DariusPeriodIndicatorReportDefinition def, String key) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().removeDimension(key);
		saveReportDefinition(def);
	}
	
	
	public static void addColumn(DariusPeriodIndicatorReportDefinition def, String key, String displayName, CohortIndicator indicator, Map<String, String> dimensionOptions) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().addColumn(key, displayName, new Mapped<CohortIndicator>(indicator, IndicatorUtil.periodIndicatorMappings()), dimensionOptions);
		saveDataSetDefinition(def);
	}
	
	
	public static void removeColumn(DariusPeriodIndicatorReportDefinition def, String key) {
		ensureDataSetDefinition(def);
		def.getIndicatorDataSetDefinition().removeColumn(key);
		saveDataSetDefinition(def);
	}

	
	// ==== PRIVATE ====

	private static void ensureDataSetDefinition(DariusPeriodIndicatorReportDefinition def) {
	    if (def.getIndicatorDataSetDefinition() == null) {
	    	def.setupDataSetDefinition();
	    	saveDataSetDefinition(def);
	    	saveReportDefinition(def);
	    }
    }

	private static void saveReportDefinition(ReportDefinition def) {
	    Context.getService(ReportService.class).saveReportDefinition(def);
    }

	private static void saveDataSetDefinition(DariusPeriodIndicatorReportDefinition def) {
		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(def.getIndicatorDataSetDefinition());
    }

}
