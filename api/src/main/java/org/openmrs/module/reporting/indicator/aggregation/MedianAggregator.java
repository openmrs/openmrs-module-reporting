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
