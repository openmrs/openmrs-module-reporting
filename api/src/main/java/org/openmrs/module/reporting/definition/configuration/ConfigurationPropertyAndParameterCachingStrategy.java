/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
