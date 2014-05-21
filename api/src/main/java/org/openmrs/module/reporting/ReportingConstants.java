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
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Constants required by this module
 */
public class ReportingConstants implements GlobalPropertyListener {

    protected static final Log log = LogFactory.getLog(ReportingConstants.class);

	private static transient Map<String, Object> gpCache = new HashMap<String, Object>();

	// Global Property Names
	public static final String GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES = "reporting.preferredIdentifierTypes";
	public static final String GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS = "report.deleteReportsAgeInHours";
	public static final String GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN = "reporting.maxReportsToRun";
	public static final String GLOBAL_PROPERTY_MAX_CACHED_REPORTS = "reporting.maxCachedReports";
	public static final String GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE = "reporting.dataEvaluationBatchSize";
	public static final String GLOBAL_PROPERTY_INCLUDE_DATA_EXPORTS = "reporting.includeDataExportsAsDataSetDefinitions";
	public static final String GLOBAL_PROPERTY_RUN_REPORT_COHORT_FILTER_MODE = "reporting.runReportCohortFilterMode";
	public static final String GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT = "reporting.defaultDateFormat";
    public static final String GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION = "reporting.testPatientsCohortDefinition";
	public static final String DEFAULT_LOCALE_GP_NAME = "reporting.defaultLocale";

	public static final List<String> CACHED_PROPERTIES = Arrays.asList(
			GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT, DEFAULT_LOCALE_GP_NAME, GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE
	);

	// Constants used within sessions to key report data that can be retrieved
	public static final String OPENMRS_REPORT_DATA = "__openmrs_report_data";
	public static final String OPENMRS_REPORT_ARGUMENT = "__openmrs_report_argument";
	public static final String OPENMRS_LAST_REPORT_URL = "__openmrs_last_report_url";

	// Some default parameters used by multiple reporting objects 
	public static final Parameter START_DATE_PARAMETER = new Parameter("startDate", "Start date", Date.class);
	public static final Parameter END_DATE_PARAMETER = new Parameter("endDate", "End date", Date.class);
	public static final Parameter LOCATION_PARAMETER = new Parameter("location", "Location", Location.class);

    // Global property accessor methods
	
	public static final List<PatientIdentifierType> GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES() {
		String propertyValue = Context.getAdministrationService().getGlobalProperty(GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES);
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
		return getPropertyValueAsInt(GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN, 2);
	}
	
	public static final int GLOBAL_PROPERTY_MAX_CACHED_REPORTS() {
		return getPropertyValueAsInt(GLOBAL_PROPERTY_MAX_CACHED_REPORTS, 10);
	}

	public static final int GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE() {
		if (gpCache.containsKey(GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE)) {
			return (Integer)gpCache.get(GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE);
		}
		else {
			int ret = getPropertyValueAsInt(GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, 1000);
			gpCache.put(GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, ret);
			return ret;
		}
	}
	
	public static final boolean GLOBAL_PROPERTY_INCLUDE_DATA_EXPORTS() {
		return getPropertyValueAsBoolean(GLOBAL_PROPERTY_INCLUDE_DATA_EXPORTS, false);
	}

    // this property is fetched a lot, so we cache it
    public static final Locale GLOBAL_PROPERTY_DEFAULT_LOCALE() {
        if (gpCache.containsKey(DEFAULT_LOCALE_GP_NAME)) {
            return (Locale)gpCache.get(DEFAULT_LOCALE_GP_NAME);
        }
        else {
			Locale locale = getPropertyValueAsLocale(DEFAULT_LOCALE_GP_NAME, null);
			gpCache.put(DEFAULT_LOCALE_GP_NAME, locale);
			return locale;
        }
    }

	// this property is fetched a lot, so we cache it
	public static final String GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT() {
		if (gpCache.containsKey(GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT)) {
			return (String)gpCache.get(GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT);
		}
		else {
			String df = getPropertyValueAsString(GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT);
			gpCache.put(GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT, df);
			return df;
		}
	}

    public static final CohortDefinition GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION() {
        String prop = getPropertyValueAsString(GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION);
        CohortDefinition cohortDefinition;
        if (StringUtils.hasText(prop)) {
            if (prop.startsWith("library:")) {
                prop = prop.substring(prop.indexOf(':') + 1);
                cohortDefinition = Context.getRegisteredComponents(AllDefinitionLibraries.class).get(0).getDefinition(CohortDefinition.class, prop);
                if (cohortDefinition == null) {
                    throw new IllegalStateException("Global property " + GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION + " refers to a library definition that cannot be found: " + prop);
                }
            }
            else {
                // this is the UUID of a saved CohortDefinition
                cohortDefinition = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(prop);
                if (cohortDefinition == null) {
                    throw new IllegalStateException("Global property " + GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION + " refers to a cohort definition that cannot be found by uuid: " + prop);
                }
            }
        } else {
            return null;
        }
        if (cohortDefinition.getParameters().size() > 0) {
            throw new IllegalStateException("Global property " + GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION + " must refer to a cohort definition with no parameters, but this has " + cohortDefinition.getParameters().size() + ": " + prop);
        }
        return cohortDefinition;
    }

    @Override
    public boolean supportsPropertyName(String s) {
		return CACHED_PROPERTIES.contains(s);
    }

    @Override
    public void globalPropertyChanged(GlobalProperty globalProperty) {
        gpCache.clear();
    }

    @Override
    public void globalPropertyDeleted(String s) {
		gpCache.clear();
    }

	private static String getPropertyValueAsString(String propertyName) {
		return Context.getAdministrationService().getGlobalProperty(propertyName);
	}

	private static int getPropertyValueAsInt(String propertyName, int defaultValue) {
		String propertyValue = getPropertyValueAsString(propertyName);
		if (StringUtils.hasText(propertyValue)) {
			try {
				return Integer.parseInt(propertyValue);
			}
			catch (Exception e) {
				log.warn("Invalid setting <" + propertyValue + "> found for global property: " + propertyName + ".  An Integer is required.  Using default of " + defaultValue);
			}
		}
		return defaultValue;
	}

	private static boolean getPropertyValueAsBoolean(String propertyName, boolean defaultValue) {
		String propertyValue = getPropertyValueAsString(GLOBAL_PROPERTY_INCLUDE_DATA_EXPORTS);
		if (StringUtils.hasText(propertyValue)) {
			try {
				return Boolean.parseBoolean(propertyValue);
			}
			catch (Exception e) {
				log.warn("Invalid setting <" + propertyValue + "> found for global property: " + propertyName + ".  A Boolean is required.  Using default of " + defaultValue);
			}
		}
		return defaultValue;
	}

	private static Locale getPropertyValueAsLocale(String propertyName, Locale defaultValue) {
		String propertyValue = getPropertyValueAsString(propertyName);
		if (StringUtils.hasText(propertyValue)) {
			try {
				return new Locale(propertyValue);
			}
			catch (Exception e) {
				log.warn("Invalid setting <" + propertyValue + "> found for global property: " + propertyName + ".  A Locale is required.  Using default of " + defaultValue);
			}
		}
		return defaultValue;
	}

	public static void clearGlobalPropertyCache() {
		gpCache.clear();
	}
}
