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
package org.openmrs.module.reporting.idset.service;

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
import org.openmrs.module.reporting.idset.EvaluatedIdSet;
import org.openmrs.module.reporting.idset.definition.IdSetDefinition;
import org.openmrs.module.reporting.idset.evaluator.IdSetDefinitionEvaluator;
import org.openmrs.module.reporting.idset.persister.IdSetDefinitionPersister;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the IdSetDefinitionService API
 */
public class IdSetDefinitionServiceImpl extends BaseDefinitionService<IdSetDefinition> implements IdSetDefinitionService {
	
	private static Log log = LogFactory.getLog(IdSetDefinitionServiceImpl.class);

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<IdSetDefinition> getDefinitionType() {
		return IdSetDefinition.class;
	}

	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends IdSetDefinition>> getDefinitionTypes() {
		List<Class<? extends IdSetDefinition>> ret = new ArrayList<Class<? extends IdSetDefinition>>();
		for (IdSetDefinitionEvaluator e : HandlerUtil.getHandlersForType(IdSetDefinitionEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						ret.add((Class<? extends IdSetDefinition>) type);
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
	public <D extends IdSetDefinition> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getIdSetDefinition(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public IdSetDefinition getDefinitionByUuid(String uuid) throws APIException {
		for (IdSetDefinitionPersister p : getAllPersisters()) {
			IdSetDefinition cd = p.getIdSetDefinitionByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public List<IdSetDefinition> getAllDefinitions(boolean includeRetired) {
		List<IdSetDefinition> ret = new ArrayList<IdSetDefinition>();
		for (IdSetDefinitionPersister p : getAllPersisters()) {
			ret.addAll(p.getAllIdSetDefinitions(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (IdSetDefinitionPersister p : getAllPersisters()) {
			i += p.getNumberOfIdSetDefinitions(includeRetired);
		}
		return i;
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	public List<IdSetDefinition> getDefinitions(String name, boolean exactMatchOnly) {
		List<IdSetDefinition> ret = new ArrayList<IdSetDefinition>();
		for (IdSetDefinitionPersister p : getAllPersisters()) {
			ret.addAll(p.getIdSetDefinitions(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends IdSetDefinition> D saveDefinition(D definition) throws APIException {
		log.debug("Saving idSet definition: " + definition + " of type " + definition.getClass());
		return (D)getPersister(definition.getClass()).saveIdSetDefinition(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	public void purgeDefinition(IdSetDefinition definition) {
		getPersister(definition.getClass()).purgeIdSetDefinition(definition);
	}

	/**
	 * 	This is the main method which should be used to evaluate a IdSetDefinition
	 *  - retrieves all evaluation parameter values from the class and the EvaluationContext
	 *  - checks whether a idSet with this configuration exists in the cache (if caching is supported)
	 *  - returns the cached idSet if found
	 *  - otherwise, delegates to the appropriate IdSetDefinitionEvaluator and evaluates the result
	 *  - caches the result (if caching is supported)
	 * 
	 * @see getCacheKey(EvaluationContext)
     * @see IdSetDefinitionEvaluator#evaluate(EvaluationContext)
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	public EvaluatedIdSet evaluate(IdSetDefinition definition, EvaluationContext context) throws EvaluationException {
		
		// Retrieve IdSetDefinitionEvaluator which can evaluate this IdSetDefinition
		IdSetDefinitionEvaluator evaluator = HandlerUtil.getPreferredHandler(IdSetDefinitionEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No IdSetDefinitionEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}

		// Clone IdSetDefinition and set all properties from the Parameters in the EvaluationContext
		IdSetDefinition clonedDefinition = DefinitionUtil.clone(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (context != null && context.containsParameter(p.getName())) {
				value = context.getParameterValue(p.getName());
			}
			ReflectionUtil.setPropertyValue(clonedDefinition, p.getName(), value);
		}
		
		// Retrieve from cache if possible, otherwise evaluate
		EvaluatedIdSet idSet = null;
		if (context != null) {
			Caching caching = clonedDefinition.getClass().getAnnotation(Caching.class);
			if (caching != null && caching.strategy() != NoCachingStrategy.class) {
				try {
					CachingStrategy strategy = caching.strategy().newInstance();
					String cacheKey = strategy.getCacheKey(clonedDefinition);
					if (cacheKey != null) {
						idSet = (EvaluatedIdSet) context.getFromCache(cacheKey);
					}
					if (idSet == null) {
						idSet = evaluator.evaluate(clonedDefinition, context);
						context.addToCache(cacheKey, idSet);
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
		if (idSet == null) {
			idSet = evaluator.evaluate(clonedDefinition, context);
		}
		return idSet;
	}

	/**
	 * @see BaseDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public EvaluatedIdSet evaluate(Mapped<? extends IdSetDefinition> definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedIdSet)super.evaluate(definition, context);
	}

	/**
	 * @return the IdSetDefinitionPersister for the passed IdSetDefinition
	 * @throws APIException if no matching persister is found
	 */
	protected IdSetDefinitionPersister getPersister(Class<? extends IdSetDefinition> definition) {
		IdSetDefinitionPersister persister = HandlerUtil.getPreferredHandler(IdSetDefinitionPersister.class, definition);
		if (persister == null) {
			throw new APIException("No IdSetDefinitionPersister found for <" + definition + ">");
		}
		return persister;
	}
	
	/**
	 * @return all IdSetDefinitionPersisters
	 */
	protected List<IdSetDefinitionPersister> getAllPersisters() {	
		return HandlerUtil.getHandlersForType(IdSetDefinitionPersister.class, null);
	}
}
