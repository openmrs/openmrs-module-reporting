/*
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

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.List;
import java.util.Map;

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
	 * @return the privilege required to view or evaluate this report or null if no specific privilege required
	 */
	String getRequiredPrivilege();

	/**
	 * This method provides a mechanism to validate input parameters,
	 * transform input parameters, or provide any other custom logic
	 * needed to set up the appropriate EvaluationContext that should
	 * be used when running this report.
	 * @return the EvaluationContext to use for the report.
	 */
	EvaluationContext initializeContext(Map<String, Object> parameters);

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
     * This is used to determine whether to build/save the report definition on module startup. Version should be something
     * like "1.0" or "1.1-SNAPSHOT". (Any version with "-SNAPSHOT" indicates it is under active development and will be
     * built/saved every time the module is started.)
     *
     * @return what version of this report we are at
     */
    String getVersion();

}
