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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Constants required by this module
 */
public class ReportingConstants implements GlobalPropertyListener {

    protected static final Log log = LogFactory.getLog(ReportingConstants.class);

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
	
	public static final int GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN() {
		String propertyValue = Context.getAdministrationService().getGlobalProperty("reporting.maxReportsToRun");
		if (StringUtils.hasText(propertyValue)) {
			try {
				return Integer.parseInt(propertyValue);
			}
			catch (Exception e) {
				// Do nothing
			}
		}
		return 2;
	}
	
	public static final int GLOBAL_PROPERTY_MAX_CACHED_REPORTS() {
		String propertyValue = Context.getAdministrationService().getGlobalProperty("reporting.maxCachedReports");
		if (StringUtils.hasText(propertyValue)) {
			try {
				return Integer.parseInt(propertyValue);
			}
			catch (Exception e) {
				// Do nothing
			}
		}
		return 10;
	}

	/**
	 * @return for data definition evaluations, this determines whether to run these in batches and what the
	 * size of those batches should be.  A value of <= 0 indicates that no batching is desired.
	 */
	public static final int GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE() {
		String propertyValue = Context.getAdministrationService().getGlobalProperty("reporting.dataEvaluationBatchSize");
		if (StringUtils.hasText(propertyValue)) {
			try {
				return Integer.parseInt(propertyValue);
			}
			catch (Exception e) {
				// Do nothing
			}
		}
		return 1000;
	}
	
	public static final boolean GLOBAL_PROPERTY_INCLUDE_DATA_EXPORTS() {
		return "true".equals(Context.getAdministrationService().getGlobalProperty("reporting.includeDataExportsAsDataSetDefinitions"));
	}

    public static final String DEFAULT_LOCALE_GP_NAME = "reporting.defaultLocale";
    private static boolean hasCachedDefaultLocale = false;
    private static transient Locale cachedDefaultLocale = null;

    // this property is fetched a lot, so we cache it
    public static final Locale GLOBAL_PROPERTY_DEFAULT_LOCALE() {
        if (hasCachedDefaultLocale) {
            return cachedDefaultLocale;
        }
        else {
            String propertyValue = Context.getAdministrationService().getGlobalProperty(DEFAULT_LOCALE_GP_NAME);
            if (StringUtils.hasText(propertyValue)) {
                try {
                    cachedDefaultLocale = new Locale(propertyValue);
                } catch (Exception e) {
                    log.warn("Unable to instantiate default locale", e);
                    cachedDefaultLocale = null;
                }
            } else {
                cachedDefaultLocale = null;
            }

            hasCachedDefaultLocale = true;
            return cachedDefaultLocale;
        }
    }

    @Override
    public boolean supportsPropertyName(String s) {
        return DEFAULT_LOCALE_GP_NAME.equals(s);
    }

    @Override
    public void globalPropertyChanged(GlobalProperty globalProperty) {
        hasCachedDefaultLocale = false;
    }

    @Override
    public void globalPropertyDeleted(String s) {
        hasCachedDefaultLocale = false;
    }
}
