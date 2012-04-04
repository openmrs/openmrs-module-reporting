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
