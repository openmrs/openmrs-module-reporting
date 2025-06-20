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
