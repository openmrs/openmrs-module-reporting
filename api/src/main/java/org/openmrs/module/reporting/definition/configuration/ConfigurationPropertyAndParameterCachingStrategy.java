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
package org.openmrs.module.reporting.definition.configuration;

import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.caching.CachingStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents a strategy for caching a particular object in the EvaluationContext.  You would typically 
 * specify which type of CachingStrategy an object uses by annotating the class with the {@link org.openmrs.module.reporting.evaluation.caching.Caching} annotation
 * @see org.openmrs.module.reporting.evaluation.caching.Caching
 */
public class ConfigurationPropertyAndParameterCachingStrategy extends ConfigurationPropertyCachingStrategy  {

	/**
	 * Implementation that creates a cache key out of the instance class name and a sorted Map of
	 * field name -> value for all fields annotated as {@link org.openmrs.module.reporting.definition.configuration.ConfigurationProperty}
	 * @see org.openmrs.module.reporting.evaluation.caching.CachingStrategy#getCacheKey(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public String getCacheKey(Definition definition, EvaluationContext context) {
		String cacheKey = super.getCacheKey(definition, context);
		Set<String> paramNames = new TreeSet<String>(context.getParameterValues().keySet());
		for (String paramName : paramNames) {
			Object value = context.getParameterValue(paramName);
			if (value != null) {
				cacheKey += "&"+paramName+"="+getStringValue(value);
			}
		}
		return cacheKey;
	}
}
