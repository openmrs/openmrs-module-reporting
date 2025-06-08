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
 * Returns the Max value of the passed objects, throwing a RuntimeException if the 
 * passed list is null or non-numeric.
 * TODO: How do we handle nulls here?
 * TODO: Can we delegate this computation to a well-tested 3rd party library?
 */
@Handler
public class MaxAggregator implements Aggregator {
	
	public MaxAggregator() {}
	
	public String getName() { 
		return "MAX";
	}
	
	public Number compute(Collection<Number> values) {
		if (values == null || values.isEmpty()) {
			throw new RuntimeException("Unable to compute a max value of a null or empty collection");
		}
		List<Number> valueList = AggregationUtil.sortNumbers(values, true);
		return valueList.get(valueList.size()-1);
	}
}
