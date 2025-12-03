/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
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
		if (Context.hasPrivilege("Manage Report Definitions")) {
			map.put("module/reporting/reports/manageReports.form", "reporting.manageReports.title");
		}
		map.put("module/reporting/definition/manageDefinitions.form?type=" + DataSetDefinition.class.getName(), "reporting.manageDataSets.title");
		map.put("module/reporting/indicators/manageIndicators.form", "reporting.manageIndicators.title");
		map.put("module/reporting/indicators/manageDimensions.form", "reporting.manageDimensions.title");
		map.put("module/reporting/definition/manageDefinitions.form?type=" + CohortDefinition.class.getName(), "reporting.manageCohortDefinitions.title");
		map.put("module/reporting/definition/manageDefinitions.form?type=" + PersonDataDefinition.class.getName(), "reporting.manageDataDefinitions.title");
		map.put("module/reporting/reports/manageReportDesigns.form", "reporting.manageReportDesigns.title");
		map.put("module/reporting/reports/manageReportProcessors.form", "reporting.manageReportProcessors.title");
		return map;
	}
	
}
