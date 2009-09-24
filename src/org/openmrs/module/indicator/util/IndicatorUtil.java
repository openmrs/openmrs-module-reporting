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
package org.openmrs.module.indicator.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.ReportingConstants;

/**
 * Indicator-related Utility methods
 */
public class IndicatorUtil {
	
	private static List<Parameter> defaultParameters = new LinkedList<Parameter>();
	
	// Statically initialize the default period indicator parameters
	static {
		defaultParameters.add(ReportingConstants.START_DATE_PARAMETER);
		defaultParameters.add(ReportingConstants.END_DATE_PARAMETER);
		defaultParameters.add(ReportingConstants.LOCATION_PARAMETER);		
	}
	
	/**
	 * Utility method which does a case-insensitive check on the given Collection of Strings
	 * for the passed testString, returning true if the Collection contains the passed string
	 * @param collection collection to loop over
	 * @param testString String to look for in the <code>collection</code>
	 * @return true/false whether the given testString is found, ignoring case
	 */
	public static boolean containsIgnoreCase(Collection<String> collection, String testString) {
		if (collection == null) {
			return false;
		}
		for (String s : collection) {
			if (s.equalsIgnoreCase(testString)) {
				return true;
			}
		}
		return false;
	}
	
	public static Map<String, Object> getDefaultParameterMappings() {
		Map<String, Object> perIndMappings = new HashMap<String, Object>();
		perIndMappings.put("startDate", "${startDate}");
		perIndMappings.put("endDate", "${endDate}");
		perIndMappings.put("location", "${location}");
		return perIndMappings;
	}

	public static List<Parameter> getDefaultParameters() {
		return defaultParameters;
	}


}
