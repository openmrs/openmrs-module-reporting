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
package org.openmrs.module.cohort.definition.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.cohort.definition.persister.CohortDefinitionPersister;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.caching.Caching;
import org.openmrs.module.evaluation.caching.CachingStrategy;
import org.openmrs.module.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.ParameterUtil;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the CohortDefinitionService API
 */
@Transactional
public class BaseCohortDefinitionService extends BaseOpenmrsService implements CohortDefinitionService {
	
	private static Log log = LogFactory.getLog(BaseCohortDefinitionService.class);
	
	/**
	 * Returns the CohortDefinitionPersister for the passed CohortDefinition
	 * @param definition
	 * @return the CohortDefinitionPersister for the passed CohortDefinition
	 * @throws APIException if no matching persister is found
	 */
	protected CohortDefinitionPersister getPersister(Class<? extends CohortDefinition> definition) {
		CohortDefinitionPersister persister = HandlerUtil.getPreferredHandler(CohortDefinitionPersister.class, definition);
		if (persister == null) {
			throw new APIException("No CohortDefinitionPersister found for <" + definition + ">");
		}
		return persister;
	}

	/** 
	 * @see CohortDefinitionService#getCohortDefinition(Class, Integer)
	 */
	@SuppressWarnings("unchecked")
	public <T extends CohortDefinition> T getCohortDefinition(Class<T> type, Integer id) throws APIException {
		return (T) getPersister(type).getCohortDefinition(id);
	}

	/** 
	 * @see CohortDefinitionService#getCohortDefinitionByUuid(String)
	 */
	public CohortDefinition getCohortDefinitionByUuid(String uuid) throws APIException {
		for (CohortDefinitionPersister p : HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, null)) {
			CohortDefinition cd = p.getCohortDefinitionByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}

	/** 
	 * @see CohortDefinitionService#getAllCohortDefinitions(boolean)
	 */
	public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired) {
		List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
		for (CohortDefinitionPersister p : HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, null)) {
			ret.addAll(p.getAllCohortDefinitions(includeRetired));
		}
		return ret;
	}

	/** 
	 * @see CohortDefinitionService#getCohortDefinitionByName(String, boolean)
	 */
	public List<CohortDefinition> getCohortDefinitionByName(String name, boolean exactMatchOnly) {
		List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
		for (CohortDefinitionPersister p : HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, null)) {
			ret.addAll(p.getCohortDefinitionByName(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see CohortDefinitionService#saveCohortDefinition(CohortDefinition)
	 */
	public CohortDefinition saveCohortDefinition(CohortDefinition definition) throws APIException {
		return getPersister(definition.getClass()).saveCohortDefinition(definition);
	}

	/** 
	 * @see CohortDefinitionService#purgeCohortDefinition(CohortDefinition)
	 */
	public void purgeCohortDefinition(CohortDefinition definition) {
		getPersister(definition.getClass()).purgeCohortDefinition(definition);
	}
	
	/**
	 * Convenience method which accepts a Mapped<CohortDefinition>, and an initial EvaluationContext to evaluate
     * @see evaluate(CohortDefinition, EvaluationContext)
	 */
	public Cohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext evalContext) throws APIException {
		EvaluationContext childContext = EvaluationContext.cloneForChild(evalContext, definition);
		log.debug("Evaluating CohortDefinition: " + definition.getParameterizable() + "(" + evalContext.getParameterValues() + ")");
		return evaluate(definition.getParameterizable(), childContext);
	}
	
	/**
	 * This is the main method which should be used to evaluate a CohortDefinition
	 *  - retrieves all evaluation parameter values from the class and the EvaluationContext
	 *  - checks whether a cohort with this configuration exists in the cache (if caching is supported)
	 *  - returns the cached cohort if found
	 *  - otherwise, delegates to the appropriate CohortDefinitionEvaluator and evaluates the result
	 *  - caches the result (if caching is supported)
	 * 
	 * Implementing classes should override the evaluateCohort(EvaluationContext) method
	 * @see getCacheKey(EvaluationContext)
     * @see CohortDefinitionEvaluator#evaluate(EvaluationContext)
	 */
	public Cohort evaluate(CohortDefinition definition, EvaluationContext evalContext) throws APIException {
		
		// Retrieve CohortDefinitionEvaluator which can evaluate this CohortDefinition
		CohortDefinitionEvaluator evaluator = HandlerUtil.getPreferredHandler(CohortDefinitionEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No CohortDefinitionEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}

		// Clone CohortDefinition and set all properties from the Parameters in the EvaluationContext
		CohortDefinition clonedDefinition = ParameterUtil.cloneParameterizable(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (evalContext != null && evalContext.containsParameter(p.getName())) {
				value = evalContext.getParameterValue(p.getName());
			}
			ParameterUtil.setAnnotatedFieldFromParameter(clonedDefinition, p, value);
		}
		
		// Retrieve from cache if possible, otherwise evaluate
		Cohort c = null;
		if (evalContext != null) {
			Caching caching = clonedDefinition.getClass().getAnnotation(Caching.class);
			if (caching != null && caching.strategy() != NoCachingStrategy.class) {
				try {
					CachingStrategy strategy = caching.strategy().newInstance();
					String cacheKey = strategy.getCacheKey(clonedDefinition);
					if (cacheKey != null) {
						c = (Cohort) evalContext.getFromCache(cacheKey);
					}
					if (c == null) {
						c = evaluator.evaluate(clonedDefinition, evalContext);
						evalContext.addToCache(cacheKey, c);
					}
				}
				catch (Exception e) {
					log.warn("An error occurred while attempting to access the cache.", e);
				}
			}
		}
		if (c == null) {
			c = evaluator.evaluate(clonedDefinition, evalContext);
		}
		if (evalContext != null && evalContext.getBaseCohort() != null && c != null) {
			c = Cohort.intersect(c, evalContext.getBaseCohort());
		}
		
		return c;
	}
}
