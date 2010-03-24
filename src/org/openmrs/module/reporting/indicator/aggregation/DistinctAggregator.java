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
import java.util.HashSet;
import java.util.Set;

import org.openmrs.annotation.Handler;

/**
 * Returns the number of distinct objects in the passed List,
 * as determined by Set equality, or 0 if the passed list is null
 */
@Handler
public class DistinctAggregator implements Aggregator {
	
	public DistinctAggregator() {}
	
	public String getName() { 
		return "DISTINCT";
	}	
	
	public Number compute(Collection<Number> values) {
		if (values != null) {
			Set<Number> valueSet = new HashSet<Number>(values);
			return valueSet.size();
		}
		return 0;
	}
	
}
