/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.report.manager;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class contains the logic necessary to set-up and tear down any reports registered as
 * ReportManager components automatically
 */
@Component
public class ReportManagerInitializer {

	protected Log log = LogFactory.getLog(getClass());

	@Autowired
    private ReportService reportService;

	@Autowired
    private ReportDefinitionService reportDefinitionService;

	@Autowired
    private SerializedObjectDAO serializedObjectDAO;

	@Autowired
    private AdministrationService adminService;

	@Autowired(required = false)
	private List<ReportManager> reportManagers;

	/**
	 * Primary method called when registered reports should be re-built and saved to the database
	 */
    public void setupReports() {
		if (reportManagers != null) {
			for (ReportManager rm : reportManagers) {
				setupReport(rm);
			}
		}
    }

	/**
	 * Sets up a single report, overwriting the existing report if the version is greater
	 */
	public void setupReport(ReportManager rm) {

		String gpName = "reporting.reportManager." + rm.getUuid() + ".version";
		GlobalProperty gp = adminService.getGlobalPropertyObject(gpName);
		if (gp == null) {
			gp = new GlobalProperty(gpName, "");
		}

		if (rm.getVersion().contains("-SNAPSHOT") || !gp.getPropertyValue().equals(rm.getVersion())) {

			ReportDefinition reportDefinition = rm.constructReportDefinition();
			log.info("Updating " + reportDefinition.getName() + " to version " + rm.getVersion());

			ReportDefinition existing = reportDefinitionService.getDefinitionByUuid(reportDefinition.getUuid());
			if (existing != null) {
				// we need to overwrite the existing, rather than purge-and-recreate, to avoid deleting old ReportRequests
				log.debug("Overwriting existing ReportDefinition");
				reportDefinition.setId(existing.getId());
				Context.evictFromSession(existing);
			}
			else {
				// incompatible class changes for a serialized object could mean that getting the definition returns null
				// and some serialization error gets logged. In that case we want to overwrite the invalid serialized definition
				SerializedObject invalidSerializedObject = serializedObjectDAO.getSerializedObjectByUuid(reportDefinition.getUuid());
				if (invalidSerializedObject != null) {
					reportDefinition.setId(invalidSerializedObject.getId());
					Context.evictFromSession(invalidSerializedObject);
				}
			}

			reportDefinitionService.saveDefinition(reportDefinition);

			// purging a ReportDesign doesn't trigger any extra logic, so we can just purge-and-recreate here
			List<ReportDesign> existingDesigns = reportService.getReportDesigns(reportDefinition, null, true);
			if (existingDesigns.size() > 0) {
				log.debug("Deleting " + existingDesigns.size() + " old designs for " + reportDefinition.getName());
				for (ReportDesign design : existingDesigns) {
					reportService.purgeReportDesign(design);
				}
			}

			List<ReportDesign> designs = rm.constructReportDesigns(reportDefinition);
			for (ReportDesign design : designs) {
				reportService.saveReportDesign(design);
			}

			gp.setPropertyValue(rm.getVersion());
			adminService.saveGlobalProperty(gp);
		}
	}
}
