package org.openmrs.module.reporting.web.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
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
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("module/reporting/dashboard/index.form", "reporting.reportDashboard.title");
		map.put("module/reporting/reports/manageReportQueue.htm", "reporting.manageReportQueue.title");
		map.put("module/reporting/reports/reportHistory.form", "reporting.reportHistory.title");
		map.put("module/reporting/reports/manageScheduledReports.form", "reporting.manageTasks.title");
		if (Context.hasPrivilege("Manage Report Definitions")) {
			map.put("module/reporting/reports/manageReports.form", "reporting.manageReports.title");
		}
		return map;
	}
}
