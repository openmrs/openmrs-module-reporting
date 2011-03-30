package org.openmrs.module.reporting.report.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

public class ReportUtil {
	
	private static CohortDefinition getCohortDefinition(String name) {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		for (CohortDefinition def : service.getDefinitions(name, true)) {
			return def;
		}
		return null;
	}
	
	public static abstract class InitialDataElement {
		
		private Class<?> clazz;
		
		private String name;
		
		private Boolean alreadyDone = false;
		
		public InitialDataElement(Class<?> clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}
		
		public abstract void apply();
		
		public Class<?> getClazz() {
			return clazz;
		}
		
		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Boolean getAlreadyDone() {
			return alreadyDone;
		}
		
		public void setAlreadyDone(Boolean alreadyDone) {
			this.alreadyDone = alreadyDone;
		}
		
		public boolean equals(InitialDataElement other) {
			return clazz.equals(other.clazz) && name.equals(other.name);
		}
		
	}
	
	public static String toCsv(DataSet dataset) throws Exception {
		ReportRenderer rr = new CsvReportRenderer();
		ReportData rd = new ReportData();
		rd.setDataSets(new HashMap<String, DataSet>());
		rd.getDataSets().put("dataset", dataset);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		rr.render(rd, null, out);
		return out.toString();
	}
	
}
