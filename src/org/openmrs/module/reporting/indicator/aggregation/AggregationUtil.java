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
