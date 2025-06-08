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

import java.util.Collection;
import java.util.List;

import org.openmrs.annotation.Handler;

/**
 * Returns the Median value of the passed objects, throwing a RuntimeException if the 
 * passed list is null or non-numeric.
 * TODO: How do we handle nulls here?
 * TODO: Can we delegate this computation to a well-tested 3rd party library?
 */
@Handler
public class MedianAggregator implements Aggregator {
	
	public MedianAggregator() {}
	
	public String getName() { 
		return "MEDIAN";
	}
	
	
	public Number compute(Collection<Number> values) {
		if (values == null) {
			throw new RuntimeException("Unable to compute a median value of a null collection");
		}
		if (values.size() == 0) {
		    return Double.valueOf(Double.valueOf(0)/0);
		}
		List<Number> valueList = AggregationUtil.sortNumbers(values, true);
		if (valueList.size() % 2 == 1) {
		    return valueList.get(valueList.size()/2);
		} 
		else {
		    Number lowerMiddle = valueList.get( valueList.size()/2 );
            Number upperMiddle = valueList.get( valueList.size()/2 - 1 );
            return (Number) ((lowerMiddle.doubleValue() + upperMiddle.doubleValue()) / 2);
		}
	}
	

}
