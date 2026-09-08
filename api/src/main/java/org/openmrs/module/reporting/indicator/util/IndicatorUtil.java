/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

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

	
	/**
	 * @return all combinations of dimension options for the passed Map
	 * @should return all combinations of dimension options
	 */
	public static List<String> compileColumnDimensionOptions(Map<String, List<String>> dimensionsAndOptions) {
		
		List<String> options = new ArrayList<String>();
		
		if (dimensionsAndOptions != null && !dimensionsAndOptions.isEmpty()) {
			
			// Iterate across each Dimension and determine which dimensions should be combined
			List<String> combinations = new ArrayList<String>();
			compileDimensionCombinations(0, "", new ArrayList<String>(dimensionsAndOptions.keySet()), combinations);
			
			// For each combination pairing, include the cross-products
			for (String combination : combinations) {
				String[] dimKeys = combination.split("\\|");  // For example:  Gender,Age,Location
				compileOptionCombinations(0, "", dimKeys, dimensionsAndOptions, options);
			}
		}
		
		return options;
	}

	/**
	 * Called recursively to identify all combinations of dimensions
	 */
	private static void compileDimensionCombinations(int startingIndex, String prefix, List<String> dimensions, List<String> combinations) {
		for (int i=startingIndex; i<dimensions.size(); i++) {
			String key = prefix + (prefix.equals("") ? "" : "|") + dimensions.get(i);
			combinations.add(key);
			if ((i+1) < dimensions.size()) {
				compileDimensionCombinations(i+1, key, dimensions, combinations);
			}
		}
	}
	
	/**
	 * Called recursively to identify all combinations of dimension options
	 */
	private static void compileOptionCombinations(int index, String prefix, String[] dimensionKeys, 
												  Map<String, List<String>> dimensionsAndOptions, List<String> options) {
		if (index < dimensionKeys.length) {
			List<String> currentKeys = dimensionsAndOptions.get(dimensionKeys[index]);
			if (currentKeys != null) {
				for (int j=0; j<currentKeys.size(); j++) {
					String option = currentKeys.get(j);
					String newPrefix = prefix + (ObjectUtil.isNull(prefix) ? "" : ",") + dimensionKeys[index] + "=" + option;
					if (index == dimensionKeys.length-1) {
						options.add(newPrefix);
					}
					else {
						compileOptionCombinations(index+1, newPrefix, dimensionKeys, dimensionsAndOptions, options);
					}
				}
			}
		}
	}
}
