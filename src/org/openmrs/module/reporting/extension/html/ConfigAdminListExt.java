package org.openmrs.module.reporting.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

public class ConfigAdminListExt extends AdministrationSectionExt {

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "reporting.config.title";
	}
	
	public String getRequiredPrivilege() {
		return "Manage Reports";
	}
	
	public Map<String, String> getLinks() {
		// Using linked hash map to keep order of links
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("module/reporting/config/manageReports.form", "reporting.manageReports.title");
		map.put("module/reporting/config/manageDataSets.form", "reporting.manageDataSets.title");
		map.put("module/reporting/config/manageIndicators.form", "reporting.manageIndicators.title");
		map.put("module/reporting/config/manageDimensions.form", "reporting.manageDimensions.title");
		map.put("module/reporting/config/manageCohortDefinitions.form", "reporting.manageCohortDefinitions.title");
		return map;
	}
	
}
