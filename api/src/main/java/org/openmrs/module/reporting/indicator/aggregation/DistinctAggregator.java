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
