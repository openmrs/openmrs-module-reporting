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
package org.openmrs.module.cohort.definition.configuration;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.util.CohortDefinitionUtil;
import org.openmrs.module.evaluation.caching.Caching;
import org.openmrs.module.evaluation.caching.CachingStrategy;

/**
 * Represents a strategy for caching a particular object
 * in the EvaluationContext.  You would typically specify which
 * type of CachingStrategy an object uses by annotating
 * the class with the {@link Caching} annotation
 * 
 * @see Caching
 */
public class ConfigurationPropertyCachingStrategy implements CachingStrategy  {
	
	/**
	 * Implementation that creates a cache key out of the 
	 * instance class name and a sorted Map of field name -> value
	 * for all fields annotated as {@link ConfigurationProperty}
	 * @see CachingStrategy#getCacheKey(java.lang.Object)
	 */
	public String getCacheKey(Object o) {
		if (o == null || !(o instanceof CohortDefinition)) {
			throw new IllegalArgumentException("Unable to getCacheKey for object that is null or not a CohortDefinition");
		}
		List<Property> props = CohortDefinitionUtil.getConfigurationProperties((CohortDefinition)o);
		Map<String, Object> m = new TreeMap<String, Object>();
		for (Property p : props) {
			if (p.getValue() != null) {
				m.put(p.getField().getName(), p.getValue());
			}
		}
		return o.getClass().getName() + m.toString();
	}
}
