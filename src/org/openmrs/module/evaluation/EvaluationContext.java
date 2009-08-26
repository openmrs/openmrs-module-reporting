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
package org.openmrs.module.evaluation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.ParameterException;
import org.openmrs.module.evaluation.parameter.Parameterizable;

/**
 * The EvaluationContext provides the following capabilities: - A baseCohort, i.e. the universe of
 * patients relevant to this context (defaults to all patients) - An in-memory cache which can be
 * used to persist and retrieve objects. Note that this cache is cleared whenever any changes are
 * made to baseCohort or any parameter values. - Capabilities to add, remove, and retrieve parameter
 * values - Capabilities to evaluate parametric expressions, e.g. ${someDateParameterName+30d}
 */
public class EvaluationContext {
	
	/* Logger */
	private static Log log = LogFactory.getLog(EvaluationContext.class);	
	
	// *******************
	// PROPERTIES 
	// *******************
	
	// Set a limit on the number of rows to evaluate 
	private Integer limit = new Integer(0);

	// Base cohort to use for evalution
	private Cohort baseCohort;	
	
	// Parameter values entered by user (or defaulted)
	private Map<String, Object> parameterValues = new HashMap<String, Object>();

	// Generic object cache
	private transient Map<String, Object> cache = new HashMap<String, Object>();
	
	// *******************
	// CONSTRUCTORS 
	// *******************
	
	/**
	 * Default Constructor
	 */
	public EvaluationContext() { }
	
	// *******************
	// FACTORY METHOD 
	// *******************
	
	public static EvaluationContext clone(EvaluationContext initialContext) {
		EvaluationContext ec = new EvaluationContext();
		ec.setCache(initialContext.getCache());
		ec.setBaseCohort(initialContext.getBaseCohort());
		ec.setParameterValues(initialContext.getParameterValues());
		return ec;
	}
	
	/**
	 * 
	 * @param baseCohort
	 * @param parameters
	 * @return
	 */
	public static EvaluationContext cloneForChild(EvaluationContext initialContext, 
												  Mapped<? extends Parameterizable> child) {
		EvaluationContext ec = EvaluationContext.clone(initialContext);
			
		for (String paramName : child.getParameterMappings().keySet()) {
			Parameter parameter = child.getParameterizable().getParameter(paramName);
			if (parameter == null) {
				throw new ParameterException("Cannot find parameter with name <" + paramName + "> in " + child.getParameterizable());
			}
			String paramVal = child.getParameterMappings().get(paramName);
			Object eval = EvaluationUtil.evaluateExpression(paramVal, initialContext.getParameterValues(), parameter.getType());
			
			ec.addParameterValue(paramName, eval);
		}
		return ec;
	}
	
	// *******************
	// INSTANCE METHODS 
	// *******************
	
	/**
	 * Get the cache property
	 * 
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getCache() {
		return cache;
	}
	
	/**
	 * Set the cache property
	 * 
	 * @param cache
	 */
	public void setCache(Map<String, Object> cache) {
		this.cache = cache;
	}
	
	/**
	 * Add a value to the cache with a given key
	 */
	public void addToCache(String key, Object value) {
		cache.put(key, value);
	}
	
	/**
	 * Remove an entry cached with the given key
	 * 
	 * @param key
	 */
	public void removeFromCache(String key) {
		cache.remove(key);
	}
	
	/**
	 * Retrieve an entry from the cached with the given key
	 * 
	 * @param key
	 */
	public Object getFromCache(String key) {
		return cache.get(key);
	}
	
	/**
	 * Return true if a cache entry exists with the given key
	 * 
	 * @param key
	 */
	public boolean isCached(String key) {
		return cache.get(key) != null;
	}
	
	/**
	 * Clear the entire cache
	 */
	public void clearCache() {
		cache.clear();
	}
	
	/**
	 * @return the parameterValues
	 */
	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}

	/**
	 * @param parameterValues the parameterValues to set
	 */
	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}
	
	/**
	 * Returns true if a Parameter with this name has been added to the Context
	 * @param parameterName the Parameter name to lookup
	 * @return true if a Parameter with this name has been added to the Context
	 */
	public boolean containsParameter(String parameterName) {
		return this.parameterValues.containsKey(parameterName);
	}
	
	/**
	 * Add a parameter value for the given parameter name
	 * @param parameterName
	 * @param value
	 */
	public void addParameterValue(String parameterName, Object value) {
		parameterValues.put(parameterName, value);
	}
	
	/**
	 * Retrieve a Parameter by Name
	 * @param parameterName
	 */
	public Object getParameterValue(String parameterName) {
		return parameterValues.get(parameterName);
	}

	/**
	 * @return the baseCohort
	 */
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	/**
	 * @param the baseCohort
	 */
	public void setBaseCohort(Cohort baseCohort) {
		this.baseCohort = baseCohort;
	}

	/**
	 * @return the number of rows to evaluate
	 */
	public Integer getLimit() {
		return limit;
	}
	
	/**
	 * @param the 
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}


}
