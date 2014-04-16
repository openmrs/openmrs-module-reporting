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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.query.IdSet;

/**
 * The EvaluationContext provides the following capabilities: 
 * 	- A baseCohort, i.e. the universe of patients relevant to this context (defaults to all patients) 
 *  - A Set of base filters, which can be used to retrieve the base set of data to operate upon
 *  - An in-memory cache which can be used to persist and retrieve previous Evaluation results. 
 *    Note that this cache is cleared whenever any changes are made to evaluationDate, limit, baseCohort
 *    TODO: We need to be smarter than this.  We will likely lose a lot of good cache data, particular between child and parent evaluations
 *  - Capabilities to add, remove, and retrieve parameter values
 */
public class EvaluationContext implements PatientCalculationContext {
	
	/* Logger */
	protected static Log log = LogFactory.getLog(EvaluationContext.class);
	
	// *******************
	// PROPERTIES 
	// *******************
	
	// Set a limit on the number of rows to evaluate 
	private Integer limit;
	
	// Base cohort to use for evaluation
	private Cohort baseCohort;
	
	// Context-based data for the evaluation - evaluating user, date evaluated, etc
	private Map<String, Object> contextValues;
	
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
		addContextValue("now", evaluationDate);
		addContextValue("start_of_today", DateUtil.getStartOfDay(evaluationDate));
		addContextValue("end_of_today", DateUtil.getEndOfDay(evaluationDate));
		addContextValue("start_of_last_month", DateUtil.getStartOfMonth(evaluationDate, -1));
		addContextValue("end_of_last_month", DateUtil.getEndOfMonth(evaluationDate, -1));
		addContextValue("generatedBy", ObjectUtil.getNameOfUser(Context.getAuthenticatedUser()));
		addContextValue("generationDate", new Date());
	}
	
	/**
	 * Constructs a new EvaluationContext given the passed EvaluationContext
	 */
	public EvaluationContext(EvaluationContext context) {
		this.setEvaluationDate(context.getEvaluationDate());
		this.setLimit(context.getLimit());
		this.setBaseCohort(context.getBaseCohort());
		this.getParameterValues().putAll(context.getParameterValues());
		this.getContextValues().putAll(context.getContextValues());
		this.setCache(context.getCache()); // This needs to be the last call, as the above calls clears the cache
	}
	
	// *******************
	// FACTORY METHOD 
	// *******************

	/**
	 * @return a cloned EvaluationContext, replacing the parameters with those in the mapped child object as appropriate.
	 */
	public static EvaluationContext cloneForChild(EvaluationContext initialContext, Mapped<? extends Parameterizable> child) {

		if (child == null || child.getParameterizable() == null) {
			throw new APIException("The specified report could not be evaluated because one of its components has been removed from the database");
		}
		EvaluationContext ec = initialContext.shallowCopy();
		ec.setParameterValues(new HashMap<String, Object>());

		Parameterizable p = child.getParameterizable();
		Map<String, Object> m = child.getParameterMappings();
		
		for (String paramName : m.keySet()) {
			Parameter parameter = p.getParameter(paramName);
			if (parameter == null) {
				throw new ParameterException("Cannot find parameter '" + paramName + "' in " + p.getClass().getName() + " " + p.getName());
			}
			Object paramVal = m.get(paramName);
			if (paramVal instanceof String) {
				String paramValStr = (String)paramVal;
				paramVal = EvaluationUtil.evaluateExpression(paramValStr, initialContext);

				// Treat mappings that are not found as null parameter values
				if (EvaluationUtil.isExpression(paramValStr) && EvaluationUtil.stripExpression(paramValStr).equals(paramVal)) {
					paramVal = null;
				}
			}
			ec.addParameterValue(paramName, paramVal);
		}
		return ec;
	}
	
	// *******************
	// INSTANCE METHODS 
	// *******************
	
	/**
	 * @return a shallow copy of the current instance
	 */
	public EvaluationContext shallowCopy() {
		return new EvaluationContext(this);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()+"[evaluationDate=" + evaluationDate);
		for (Map.Entry<String, Object> e : getParameterValues().entrySet()) {
			if (e.getValue() != null)
				sb.append("," + e.getKey() + "->" + e.getValue() + " (" + e.getValue().getClass().getSimpleName() + ")");
		}
		sb.append("]");
		return sb.toString();
	}

	public Map<Class<? extends OpenmrsData>, IdSet<?>> getAllBaseIdSets() {
		Map<Class<? extends OpenmrsData>, IdSet<?>> ret = new LinkedHashMap<Class<? extends OpenmrsData>, IdSet<?>>();
		if (getBaseCohort() != null) {
			ret.put(Patient.class, new PatientIdSet(getBaseCohort().getMemberIds()));
		}
		return ret;
	}
	
	/**
	 * Get the cache property
	 * 
	 * @return Map<String, Object>
	 */
    @JsonIgnore
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
	 * @see PatientCalculationContext#getNow()
	 */
	@Override
	public Date getNow() {
		return this.getEvaluationDate();
	}

	/**
	 * @see PatientCalculationContext#setNow(java.util.Date)
	 */
	@Override
	public void setNow(Date date) {
		this.setEvaluationDate(date);
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
	 * @return the contextValues
	 */
	public Map<String, Object> getContextValues() {
		if (contextValues == null) {
			contextValues = new HashMap<String, Object>();
		}
		return contextValues;
	}
	
	/**
	 * @param contextValues the contextValues to set
	 */
	public void setContextValues(Map<String, Object> contextValues) {
		this.contextValues = contextValues;
	}
	
	/**
	 * Adds a value to the Context
	 */
	public void addContextValue(String key, Object value) {
		getContextValues().put(key, value);
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
	 * @param baseCohort the baseCohort
	 */
	public void setBaseCohort(Cohort baseCohort) {
		clearCache();
		this.baseCohort = baseCohort;
	}

	/**
	 * @return the number of rows to evaluate
	 */
	public Integer getLimit() {
		return limit;
	}
	
	/**
	 * @param limit the limit
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
		clearCache();
		this.evaluationDate = evaluationDate;
	}
	
}
