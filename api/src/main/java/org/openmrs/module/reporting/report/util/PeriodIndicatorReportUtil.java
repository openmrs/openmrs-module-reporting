package org.openmrs.module.reporting.report.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DelimitedKeyComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;


/**
 * Utility methods that allow you to manipulate a PeriodIndicatorReport
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
		saveDataSetDefinition(def);
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

	public static void saveDataSetDefinition(PeriodIndicatorReportDefinition def) {
		Map<String, CohortIndicatorAndDimensionColumn> sortedColumns = new TreeMap<String, CohortIndicatorAndDimensionColumn>(new DelimitedKeyComparator());
		CohortIndicatorDataSetDefinition cidsd = def.getIndicatorDataSetDefinition();
		for (CohortIndicatorAndDimensionColumn c : cidsd.getColumns()) {
			sortedColumns.put(c.getName(), c);
		}
		cidsd.setColumns(new ArrayList<CohortIndicatorAndDimensionColumn>(sortedColumns.values()));
		Context.getService(DataSetDefinitionService.class).saveDefinition(def.getIndicatorDataSetDefinition());
    }
}
