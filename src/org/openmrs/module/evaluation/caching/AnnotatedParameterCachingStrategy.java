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
package org.openmrs.module.evaluation.caching;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.evaluation.parameter.ParameterUtil;

/**
 * Represents a strategy for caching a particular object
 * in the EvaluationContext.  You would typically specify which
 * type of CachingStrategy an object uses by annotating
 * the class with the {@link Caching} annotation
 * 
 * @see Caching
 */
public class AnnotatedParameterCachingStrategy implements CachingStrategy  {
	
	/**
	 * Implementation that creates a cache key out of the 
	 * instance class name and a sorted Map of field name -> value
	 * for all fields annotated as {@link Param}
	 * @see org.openmrs.report.context.CachingStrategy#getCacheKey(java.lang.Object)
	 */
	public String getCacheKey(Object o) {
		if (o == null || !(o instanceof Parameterizable)) {
			throw new IllegalArgumentException("AnnotatedParameterCachingStrategy is only supported for Parameterizable classes");
		}
		List<Parameter> parameters = ParameterUtil.getAnnotatedParameters((Parameterizable) o);
		Map<String, Object> m = new TreeMap<String, Object>();
		for (Parameter p : parameters) {
			if (p.getDefaultValue() != null) {
				m.put(p.getName(), p.getDefaultValue());
			}
		}
		return o.getClass().getName() + m.toString();
	}
}
