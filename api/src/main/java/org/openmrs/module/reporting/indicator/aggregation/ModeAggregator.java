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
 * Returns the Mode value of the passed objects, throwing a RuntimeException if the passed list is
 * null or empty. TODO: How do we handle nulls here? TODO: Can we delegate this computation to a
 * well-tested 3rd party library?
 */
@Handler
public class ModeAggregator implements Aggregator {
	
	public ModeAggregator() {
	}
	
	public String getName() {
		return "MODE";
	}
	
	public Number compute(Collection<Number> values) {
		if (values == null || values.size() == 0) {
			throw new RuntimeException("Unable to compute a mode value of a null or empty collection");
		}
		
		Number valuesArray[] = values.toArray(new Number[] {});
		Number maxCountValue = null;
		int maxCount = 0;
		
		for (int i = 0; i < valuesArray.length; i++) {
			
			int count = 0;
			for (int j = 0; j < valuesArray.length; j++) {
				if (valuesArray[j] == valuesArray[i]) {
					count++;
				}
			}
			
			if (count > maxCount) {
				maxCount = count;
				maxCountValue = valuesArray[i];
			}
		}
		
		return maxCountValue;
	}
}
