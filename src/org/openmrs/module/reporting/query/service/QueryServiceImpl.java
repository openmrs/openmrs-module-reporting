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
package org.openmrs.module.reporting.query.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.caching.CachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.EvaluatedQuery;
import org.openmrs.module.reporting.query.definition.Query;
import org.openmrs.module.reporting.query.evaluator.QueryEvaluator;
import org.openmrs.module.reporting.query.persister.QueryPersister;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the QueryService API
 */
public class QueryServiceImpl extends BaseDefinitionService<Query> implements QueryService {
	
	private static Log log = LogFactory.getLog(QueryServiceImpl.class);

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<Query> getDefinitionType() {
		return Query.class;
	}

	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends Query>> getDefinitionTypes() {
		List<Class<? extends Query>> ret = new ArrayList<Class<? extends Query>>();
		for (QueryEvaluator e : HandlerUtil.getHandlersForType(QueryEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						ret.add((Class<? extends Query>) type);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getDefinition(Class, Integer)
	 */
	@SuppressWarnings("unchecked")
	public <D extends Query> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getQuery(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public Query getDefinitionByUuid(String uuid) throws APIException {
		for (QueryPersister p : getAllPersisters()) {
			Query cd = p.getQueryByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public List<Query> getAllDefinitions(boolean includeRetired) {
		List<Query> ret = new ArrayList<Query>();
		for (QueryPersister p : getAllPersisters()) {
			ret.addAll(p.getAllQuerys(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (QueryPersister p : getAllPersisters()) {
			i += p.getNumberOfQuerys(includeRetired);
		}
		return i;
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	public List<Query> getDefinitions(String name, boolean exactMatchOnly) {
		List<Query> ret = new ArrayList<Query>();
		for (QueryPersister p : getAllPersisters()) {
			ret.addAll(p.getQuerys(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends Query> D saveDefinition(D definition) throws APIException {
		log.debug("Saving queryResult definition: " + definition + " of type " + definition.getClass());
		return (D)getPersister(definition.getClass()).saveQuery(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	public void purgeDefinition(Query definition) {
		getPersister(definition.getClass()).purgeQuery(definition);
	}

	/**
	 * 	This is the main method which should be used to evaluate a Query
	 *  - retrieves all evaluation parameter values from the class and the EvaluationContext
	 *  - checks whether a queryResult with this configuration exists in the cache (if caching is supported)
	 *  - returns the cached queryResult if found
	 *  - otherwise, delegates to the appropriate QueryEvaluator and evaluates the result
	 *  - caches the result (if caching is supported)
	 * 
	 * @see getCacheKey(EvaluationContext)
     * @see QueryEvaluator#evaluate(EvaluationContext)
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	public EvaluatedQuery evaluate(Query definition, EvaluationContext context) throws EvaluationException {
		
		// Retrieve QueryEvaluator which can evaluate this Query
		QueryEvaluator evaluator = HandlerUtil.getPreferredHandler(QueryEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No QueryEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}

		// Clone Query and set all properties from the Parameters in the EvaluationContext
		Query clonedDefinition = DefinitionUtil.clone(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (context != null && context.containsParameter(p.getName())) {
				value = context.getParameterValue(p.getName());
			}
			ReflectionUtil.setPropertyValue(clonedDefinition, p.getName(), value);
		}
		
		// Retrieve from cache if possible, otherwise evaluate
		EvaluatedQuery queryResult = null;
		if (context != null) {
			Caching caching = clonedDefinition.getClass().getAnnotation(Caching.class);
			if (caching != null && caching.strategy() != NoCachingStrategy.class) {
				try {
					CachingStrategy strategy = caching.strategy().newInstance();
					String cacheKey = strategy.getCacheKey(clonedDefinition);
					if (cacheKey != null) {
						queryResult = (EvaluatedQuery) context.getFromCache(cacheKey);
					}
					if (queryResult == null) {
						queryResult = evaluator.evaluate(clonedDefinition, context);
						context.addToCache(cacheKey, queryResult);
					}
				}
				catch (IllegalDatabaseAccessException ie) {
					throw ie;
				}
				catch (Exception e) {
					log.warn("An error occurred while attempting to access the cache.", e);
				}
			}
		}
		if (queryResult == null) {
			queryResult = evaluator.evaluate(clonedDefinition, context);
		}
		return queryResult;
	}

	/**
	 * @see BaseDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public EvaluatedQuery evaluate(Mapped<? extends Query> definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedQuery)super.evaluate(definition, context);
	}

	/**
	 * @return the QueryPersister for the passed Query
	 * @throws APIException if no matching persister is found
	 */
	protected QueryPersister getPersister(Class<? extends Query> definition) {
		QueryPersister persister = HandlerUtil.getPreferredHandler(QueryPersister.class, definition);
		if (persister == null) {
			throw new APIException("No QueryPersister found for <" + definition + ">");
		}
		return persister;
	}
	
	/**
	 * @return all QueryPersisters
	 */
	protected List<QueryPersister> getAllPersisters() {	
		return HandlerUtil.getHandlersForType(QueryPersister.class, null);
	}
}
