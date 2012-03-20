package org.openmrs.module.reporting.web.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import org.openmrs.util.OpenmrsClassLoader;

public class ManageAdminListExt extends AdministrationSectionExt {

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "reporting.manage.title";
	}
	
	public String getRequiredPrivilege() {
		return "Manage Reports";
	}
	
	public Map<String, String> getLinks() {
		// Using linked hash map to keep order of links
		Map<String, String> map = new LinkedHashMap<String, String>();
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		map.put("module/reporting/reports/manageReports.form", "reporting.manageReports.title");
		map.put("module/reporting/definition/manageDefinitions.form?type="+DataSetDefinition.class.getName(), "reporting.manageDataSets.title");
		map.put("module/reporting/indicators/manageIndicators.form", "reporting.manageIndicators.title");
		map.put("module/reporting/indicators/manageDimensions.form", "reporting.manageDimensions.title");
		map.put("module/reporting/definition/manageDefinitions.form?type="+CohortDefinition.class.getName(), "reporting.manageCohortDefinitions.title");
		map.put("module/reporting/reports/manageReportDesigns.form", "reporting.manageReportDesigns.title");
		return map;
	}
	
}
