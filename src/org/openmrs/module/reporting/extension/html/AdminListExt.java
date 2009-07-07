package org.openmrs.module.reporting.extension.html;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

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
		// Using linked hash map to keep order of links
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("module/reporting/manageReports.list", "reporting.manageReports.title");
		map.put("module/reporting/manageDatasets.list", "reporting.manageDatasets.title");
		map.put("module/reporting/manageIndicators.list", "reporting.manageIndicators.title");
		map.put("module/reporting/manageCohortDefinitions.list", "reporting.manageCohorts.title");
		return map;
	}
	
}
