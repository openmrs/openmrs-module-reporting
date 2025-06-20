/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmrs.api.APIException;

/**
 * Aggregation-related Utility methods
 */
public class AggregationUtil {
	
	/**
	 * 
	 * @param <T>
	 * @param values
	 * @param aggregator
	 * @return
	 */
	public static Number aggregate(Collection<Number> values, Class<? extends Aggregator> aggregator) {
		
		if (values == null) {
			return null;
		}
		
		Aggregator a = null;
		if (aggregator == null) {
			throw new IllegalArgumentException("Aggregator must not be null.");
		}
	
		try {
			a = aggregator.newInstance();
	    }
    	catch (Exception e) {
    		throw new APIException("Unable to instantiate aggregator " + aggregator, e);
    	}
    	return a.compute(values);
	}
	
	/**
	 * Utility method which takes a Collection of Numbers, and returns
	 * this back as a List, in order of each Number's double value
	 * If removeNull is true, null values will be ignored and not returned
	 * If removeNull is false, null values will be sorted to the end of the list
	 * @param values The Collection of Numbers to sort
	 * @return A List of the Numbers, sorted in ascending order by double value
	 */
	public static <T extends Number>List<T> sortNumbers(Collection<T> values, boolean removeNull) {
		List<T> valueList = new ArrayList<T>(values);
		if (removeNull) {
			values.remove(null);
		}
		Collections.sort(valueList, new Comparator<T>() {
            public int compare(T n1, T n2) {
            	if (n1 == null) {
            		return n2 == null ? 0 : 1;
            	}
            	else if (n2 == null) {
            		return -1;
            	}
            	Double d1 = Double.valueOf(n1.doubleValue());
            	Double d2 = Double.valueOf(n2.doubleValue());
            	return d1.compareTo(d2);
            }
		});
		return valueList;
	}
}
