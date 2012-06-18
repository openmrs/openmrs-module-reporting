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

import org.openmrs.annotation.Handler;

/**
 * Returns the Mean value of the passed objects, throwing a RuntimeException if the 
 * passed list is null or non-numeric.
 * TODO: How do we handle nulls here?
 * TODO: Can we delegate this computation to a well-tested 3rd party library?
 */
@Handler
public class MeanAggregator implements Aggregator {
	
	public MeanAggregator() {}
	
	public String getName() { 
		return "MEAN";
	}
	
	
	public Number compute(Collection<Number> values) {
		if (values == null) {
			throw new RuntimeException("Unable to compute a mean value of a null collection");
		}
		double runningTotal = 0;
		int numTotaled = 0;
		try {
			for (Number n : values) {
				if (n != null) { // TODO: What do we do with nulls here?
					runningTotal += n.doubleValue();
					numTotaled++;
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to calculate mean value, since all values are not numeric.");
		}
		return new Double(runningTotal/numTotaled);
	}
}
