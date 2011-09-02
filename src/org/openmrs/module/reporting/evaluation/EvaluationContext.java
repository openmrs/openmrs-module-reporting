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
package org.openmrs.module.reporting.evaluation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsData;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterConstants;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;

/**
 * The EvaluationContext provides the following capabilities: - A baseCohort, i.e. the universe of
 * patients relevant to this context (defaults to all patients) - An in-memory cache which can be
 * used to persist and retrieve objects. Note that this cache is cleared whenever any changes are
 * made to baseCohort or any parameter values. - Capabilities to add, remove, and retrieve parameter
 * values - Capabilities to evaluate parametric expressions, e.g. ${someDateParameterName+30d}
 */
public class EvaluationContext {
	
	/* Logger */
	protected static Log log = LogFactory.getLog(EvaluationContext.class);
	
	// *******************
	// PROPERTIES 
	// *******************
	
	// Set a limit on the number of rows to evaluate 
	private Integer limit;
	
	// Base cohort to use for evaluation
	private Cohort baseCohort;
	
	// Base set of ids to limit the rows evaluated and returned.
	private Map<Class<? extends OpenmrsData>, Map<Integer, Integer>> baseIdSet;
	
	// Parameter values entered by user (or defaulted)
	private Map<String, Object> parameterValues;
	
	// Generic object cache
	private transient Map<String, Object> cache;
	
	// Stores the date for which the Evaluation Context was constructed
	private Date evaluationDate;
	
	// *******************
	// CONSTRUCTORS 
	// *******************
	
	/**
	 * Default Constructor
	 */
	public EvaluationContext() {
		this(new Date());
	}
	
	/**
	 * Constructor which sets the Evaluation Date to a particular date
	 */
	public EvaluationContext(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
		for (ParameterConstants c : ParameterConstants.values()) {
			addParameterValue(c.getParameterName(), c.getParameterValue(this));
		}
	}
	
	/**
	 * Constructs a new EvaluationContext given the passed EvaluationContext
	 * 
	 * @param context
	 */
	public EvaluationContext(EvaluationContext context) {
		this.setEvaluationDate(context.getEvaluationDate());
		this.setLimit(context.getLimit());
		this.setCache(context.getCache());
		this.setBaseCohort(context.getBaseCohort());
		this.setBaseIdSet(context.getBaseIdSet());
		this.getParameterValues().putAll(context.getParameterValues());
	}
	
	// *******************
	// FACTORY METHOD 
	// *******************
	
	/**
	 * Clones an evaluation context and returns it
	 * 
	 * @param initialContext the EvaluationContext to clone
	 * @return EvaluationContext the cloned EvaluationContext
	 */
	public static EvaluationContext clone(EvaluationContext initialContext) {
		return new EvaluationContext(initialContext);
	}
	
	/**
	 * Clone an EvaluationContext, replacing the parameters with those in the mapped child object as
	 * appropriate.
	 * 
	 * @param baseCohort
	 * @param parameters
	 * @return
	 */
	public static EvaluationContext cloneForChild(EvaluationContext initialContext, Mapped<? extends Parameterizable> child) {
		
		if (child == null || child.getParameterizable() == null)
			throw new APIException(
			        "The specified report could not be evaluated because one of its components has been removed from the database");
		
		EvaluationContext ec = EvaluationContext.clone(initialContext);
		
		for (String paramName : child.getParameterMappings().keySet()) {
			Parameter parameter = child.getParameterizable().getParameter(paramName);
			if (parameter == null) {
				throw new ParameterException("Cannot find parameter '" + paramName + "' in "
				        + child.getParameterizable().getClass().getName() + " " + child.getParameterizable().getName());
			}
			Object paramVal = child.getParameterMappings().get(paramName);
			if (paramVal instanceof String) {
				paramVal = EvaluationUtil.evaluateExpression(paramVal.toString(), initialContext);
			}
			ec.addParameterValue(paramName, paramVal);
		}
		return ec;
	}
	
	// *******************
	// INSTANCE METHODS 
	// *******************
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EvaluationContext[evaluationDate=" + evaluationDate);
		for (Map.Entry<String, Object> e : getParameterValues().entrySet()) {
			if (e.getValue() != null)
				sb.append("," + e.getKey() + "->" + e.getValue() + " (" + e.getValue().getClass().getSimpleName() + ")");
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Get the cache property
	 * 
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getCache() {
		if (cache == null) {
			cache = new HashMap<String, Object>();
		}
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
		getCache().put(key, value);
	}
	
	/**
	 * Remove an entry cached with the given key
	 * 
	 * @param key
	 */
	public void removeFromCache(String key) {
		getCache().remove(key);
	}
	
	/**
	 * Retrieve an entry from the cached with the given key
	 * 
	 * @param key
	 */
	public Object getFromCache(String key) {
		return getCache().get(key);
	}
	
	/**
	 * Return true if a cache entry exists with the given key
	 * 
	 * @param key
	 */
	public boolean isCached(String key) {
		return getCache().get(key) != null;
	}
	
	/**
	 * Clear the entire cache
	 */
	public void clearCache() {
		getCache().clear();
	}
	
	/**
	 * @return the parameterValues
	 */
	public Map<String, Object> getParameterValues() {
		if (parameterValues == null) {
			parameterValues = new HashMap<String, Object>();
		}
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
	 * 
	 * @param parameterName the Parameter name to lookup
	 * @return true if a Parameter with this name has been added to the Context
	 */
	public boolean containsParameter(String parameterName) {
		return getParameterValues().containsKey(parameterName);
	}
	
	/**
	 * Add a parameter value for the given parameter name
	 * 
	 * @param parameterName
	 * @param value
	 */
	public void addParameterValue(String parameterName, Object value) {
		getParameterValues().put(parameterName, value);
	}
	
	/**
	 * Retrieve a Parameter by Name
	 * 
	 * @param parameterName
	 */
	public Object getParameterValue(String parameterName) {
		return getParameterValues().get(parameterName);
	}
	
	/**
	 * @return the baseCohort
	 */
	public Cohort getBaseCohort() {
		if (getLimit() != null && getLimit() > 0) {
			return CohortUtil.limitCohort(baseCohort, getLimit());
		}
		return baseCohort;
	}
	
	/**
	 * @param the baseCohort
	 */
	public void setBaseCohort(Cohort baseCohort) {
		clearCache();
		this.baseCohort = baseCohort;
	}

	/**
	 * @return the baseIdSet
	 */
	public Map<Class<? extends OpenmrsData>, Map<Integer, Integer>> getBaseIdSet() {
		return baseIdSet;
	}

	/**
	 * @param baseIdSet the baseIdSet to set
	 */
	public void setBaseIdSet(Map<Class<? extends OpenmrsData>, Map<Integer, Integer>> baseIdSet) {
		clearCache();
		this.baseIdSet = baseIdSet;
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
		clearCache();
		this.limit = limit;
	}
	
	/**
	 * @return the evaluationDate
	 */
	public Date getEvaluationDate() {
		return evaluationDate;
	}
	
	/**
	 * @param evaluationDate the evaluationDate to set
	 */
	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}
	
}
