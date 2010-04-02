package org.openmrs.module.reporting.web.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;


public class RunAdminListExt extends AdministrationSectionExt {
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "reporting.run.title";
	}
	
	public String getRequiredPrivilege() {
		return "Run Reports";
	}
	
	public Map<String, String> getLinks() {
		// Using linked hash map to keep order of links
		Map<String, String> map = new LinkedHashMap<String, String>();
		//map.put("module/reporting/dashboard/index.form", "reporting.dashboard.title");
		//map.put("module/reporting/run/runReport.list", "reporting.runReport.title");
		map.put("module/reporting/dashboard/index.form", "reporting.runReport.title");
		map.put("module/reporting/reports/reportHistory.form", "reporting.reportHistory.title");
		map.put("module/reporting/indicators/indicatorHistory.form", "reporting.indicatorHistory.title");
		map.put("module/reporting/datasets/viewDataSet.form", "reporting.dataSetViewer.title");
		//map.put("module/reporting/reports/manageReports.form", "reporting.manage.title");
		return map;
	}
	
}
