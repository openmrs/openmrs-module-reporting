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
package org.openmrs.module.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.util.StringUtils;

/**
 * Constants required by this module
 */
public class ReportingConstants {
	
	public static final String PRIV_VIEW_REPORTS = "View Reports";
	public static final String PRIV_ADD_REPORTS = "Add Reports";
	public static final String PRIV_EDIT_REPORTS = "Edit Reports";	
	public static final String PRIV_DELETE_REPORTS = "Delete Reports";	
	public static final String PRIV_RUN_REPORTS = "Run Reports";	
	public static final String PRIV_VIEW_REPORT_OBJECTS = "View Report Objects";	
	public static final String PRIV_ADD_REPORT_OBJECTS = "Add Report Objects";	
	public static final String PRIV_EDIT_REPORT_OBJECTS = "Edit Report Objects";	
	public static final String PRIV_DELETE_REPORT_OBJECTS = "Delete Report Objects";

	public static final String REPORT_OBJECT_TYPE_PATIENTFILTER = "Patient Filter";
	public static final String REPORT_OBJECT_TYPE_PATIENTSEARCH = "Patient Search";
	public static final String REPORT_OBJECT_TYPE_PATIENTDATAPRODUCER = "Patient Data Producer";
	
	public static final String GLOBAL_PROPERTY_REPORT_XML_MACROS = "report.xmlMacros";
	public static final String GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS = "report.deleteReportsAgeInHours";
	public static final String GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN = "report.maximumSimultaneousReportsToRun";
	
	public static final String OPENMRS_REPORT_DATA = "__openmrs_report_data";
	public static final String OPENMRS_REPORT_ARGUMENT = "__openmrs_report_argument";
	public static final String OPENMRS_LAST_REPORT_URL = "__openmrs_last_report_url";
	public static final String OPENMRS_HIDE_REPORT_LINK = "__openmrs_hide_report_link";

	// Some default parameters used by multiple reporting objects 
	public static final Parameter START_DATE_PARAMETER = new Parameter("startDate", "Start date", Date.class);
	public static final Parameter END_DATE_PARAMETER = new Parameter("endDate", "End date", Date.class);
	public static final Parameter LOCATION_PARAMETER = new Parameter("location", "Location", Location.class);
	
	
	// Semi-Constants defined through global properties
	
	public static final List<PatientIdentifierType> GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES() {
		String propertyValue = Context.getAdministrationService().getGlobalProperty("reporting.preferredIdentifierTypes");
		List<PatientIdentifierType> pits = new ArrayList<PatientIdentifierType>();
		if (StringUtils.hasText(propertyValue)) {
			for (String s : propertyValue.split("\\|")) {
				PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName(s);
				if (pit != null)
					pits.add(pit);
			}
		}
		return pits;
	}
}
