/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.manager;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.List;

/**
 * This is the base interface that should be implemented by any
 * report author who wishes their ReportDefinition to be automatically
 * installed upon startup.
 */
public interface ReportManager {

    /**
     * @return the uuid of the Report
     */
    String getUuid();

	/**
	 * @return the name of the Report
	 */
	String getName();

	/**
	 * @return the description of the Report
	 */
	String getDescription();

	/**
	 * @return the parameters of the Report
	 */
	List<Parameter> getParameters();

	/**
	 * @return a ReportDefinition that may be persisted or run
	 */
	ReportDefinition constructReportDefinition();

    /**
     * @param reportDefinition this will be the same ReportDefinition returned by an earlier call to #constructReportDefinition
     * @return the ReportDesigns under which this report can be evaluated
     */
    List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition);

    /**
     * @param reportDefinition this will be the same ReportDefinition returned by an earlier call to #constructReportDefinition
     * @return the ReportRequests that should be automatically scheduled for execution
     */
    List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition);

    /**
     * This is used to determine whether to build/save the report definition on module startup. Version should be something
     * like "1.0" or "1.1-SNAPSHOT". (Any version with "-SNAPSHOT" indicates it is under active development and will be
     * built/saved every time the module is started.)
     *
     * @return what version of this report we are at
     */
    String getVersion();
}
