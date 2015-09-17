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
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.ReportUtil;

import java.util.List;

/**
 * This class contains the logic necessary to set-up and tear down a report defined as a ReportManager
 */
public class ReportManagerUtil {

	protected static Log log = LogFactory.getLog(ReportManagerUtil.class);

	/**
	 * Convenience method that can be used to automatically set up all ReportManager classes
	 * that extend the passed in interface and are registered spring beans.
	 * This enables a module author to create an interface or base class for all of their module's reports,
	 * and annotate each of these as a Component, and call this single method to set them all up in their
	 * Activator.
	 */
	public static void setupAllReports(Class<? extends ReportManager> reportManagerClass) {
		for (ReportManager reportManager : Context.getRegisteredComponents(reportManagerClass)) {
			setupReport(reportManager);
		}
	}

	/**
	 * Sets up a single report, overwriting the existing report if the version is greater
	 * Typical usage would be for a module that defines one or more reports via ReportManagers
	 * to install these in the started() method of their activator by calling this method on each
	 * ReportManager in turn.
	 */
	public static void setupReport(ReportManager rm) {

		String gpName = "reporting.reportManager." + rm.getUuid() + ".version";
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(gpName);
		if (gp == null) {
			gp = new GlobalProperty(gpName, "");
		}

		if (rm.getVersion().contains("-SNAPSHOT") || !gp.getPropertyValue().equals(rm.getVersion())) {
			ReportDefinition reportDefinition = rm.constructReportDefinition();
            List<ReportDesign> reportDesigns = rm.constructReportDesigns(reportDefinition);
            List<ReportRequest> scheduledRequests = rm.constructScheduledRequests(reportDefinition);
            log.info("Updating " + reportDefinition.getName() + " to version " + rm.getVersion());
            setupReportDefinition(reportDefinition, reportDesigns, scheduledRequests);
			gp.setPropertyValue(rm.getVersion());
			Context.getAdministrationService().saveGlobalProperty(gp);
		}
	}

    public static void setupReportDefinition(ReportDefinition reportDefinition, List<ReportDesign> designs, List<ReportRequest> scheduledRequests) {
        ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
        ReportDefinition existing = rds.getDefinitionByUuid(reportDefinition.getUuid());
        if (existing != null) {
            // we need to overwrite the existing, rather than purge-and-recreate, to avoid deleting old ReportRequests
            log.debug("Overwriting existing ReportDefinition");
            reportDefinition.setId(existing.getId());
            Context.evictFromSession(existing);
        }
        else {
            // incompatible class changes for a serialized object could mean that getting the definition returns null
            // and some serialization error gets logged. In that case we want to overwrite the invalid serialized definition
            SerializedObjectDAO serializedObjectDAO = Context.getRegisteredComponents(SerializedObjectDAO.class).get(0);
            SerializedObject invalidSerializedObject = serializedObjectDAO.getSerializedObjectByUuid(reportDefinition.getUuid());
            if (invalidSerializedObject != null) {
                reportDefinition.setId(invalidSerializedObject.getId());
                Context.evictFromSession(invalidSerializedObject);
            }
        }
        rds.saveDefinition(reportDefinition);

        // purging a ReportDesign doesn't trigger any extra logic, so we can just purge-and-recreate here
        ReportService reportService = Context.getService(ReportService.class);
        List<ReportDesign> existingDesigns = reportService.getReportDesigns(reportDefinition, null, true);
        if (existingDesigns.size() > 0) {
            log.debug("Deleting " + existingDesigns.size() + " old designs for " + reportDefinition.getName());
            for (ReportDesign design : existingDesigns) {
                reportService.purgeReportDesign(design);
            }
        }

        if (designs != null) {
            for (ReportDesign design : designs) {
                reportService.saveReportDesign(design);
            }
        }

        // Update scheduled report requests
        if (scheduledRequests != null) {
            for (ReportRequest rrTemplate : scheduledRequests) {
                ReportRequest existingRequest = reportService.getReportRequestByUuid(rrTemplate.getUuid());
                if (existingRequest == null) {
                    reportService.queueReport(rrTemplate);
                }
                else {
                    existingRequest.setReportDefinition(rrTemplate.getReportDefinition());
                    existingRequest.setPriority(rrTemplate.getPriority());
                    existingRequest.setProcessAutomatically(rrTemplate.isProcessAutomatically());
                    existingRequest.setRenderingMode(rrTemplate.getRenderingMode());
                    existingRequest.setSchedule(rrTemplate.getSchedule());
                    reportService.saveReportRequest(existingRequest);
                }
            }
        }
    }

	/**
	 * @return a new ReportDesign for an Excel template, using a file on the classpath as the template
	 */
	public static ReportDesign createExcelTemplateDesign(String reportDesignUuid, ReportDefinition reportDefinition, String resourcePath) {
		ReportDesign design = new ReportDesign();
		design.setUuid(reportDesignUuid);
		design.setName("Excel");
		design.setReportDefinition(reportDefinition);
		design.setRendererType(XlsReportRenderer.class);
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template");
		resource.setExtension("xls");
		resource.setContentType("application/vnd.ms-excel");
		resource.setContents(ReportUtil.readByteArrayFromResource(resourcePath));
		resource.setReportDesign(design);
		design.addResource(resource);
		return design;
	}

	/**
	 * @return a new ReportDesign for a standard Excel output
	 */
	public static ReportDesign createExcelDesign(String reportDesignUuid, ReportDefinition reportDefinition) {
		ReportDesign design = new ReportDesign();
		design.setUuid(reportDesignUuid);
		design.setName("Excel");
		design.setReportDefinition(reportDefinition);
		design.setRendererType(XlsReportRenderer.class);
		design.addPropertyValue(XlsReportRenderer.INCLUDE_DATASET_NAME_AND_PARAMETERS_PROPERTY, "true");
		return design;
	}

	/**
	 * @return a new ReportDesign for a standard CSV output
	 */
	public static ReportDesign createCsvReportDesign(String reportDesignUuid, ReportDefinition reportDefinition) {
		ReportDesign design = new ReportDesign();
		design.setUuid(reportDesignUuid);
		design.setName("CSV");
		design.setReportDefinition(reportDefinition);
		design.setRendererType(CsvReportRenderer.class);
		return design;
	}
}
