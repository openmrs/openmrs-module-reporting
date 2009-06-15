package org.openmrs.module.reporting.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

public class AdminListExt extends AdministrationSectionExt {

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "reporting.title";
	}
	
	public String getRequiredPrivilege() {
		return "Manage Reports";
	}
	
	public Map<String, String> getLinks() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("module/reporting/cohortDefinitions.list", "reporting.manageCohorts.title");
		map.put("module/reporting/manageDatasets.list", "reporting.manageDatasets.title");
		map.put("module/reporting/manageIndicators.list", "reporting.manageIndicators.title");
		map.put("module/reporting/manageReports.list", "reporting.manageReports.title");
		return map;
	}
	
}
