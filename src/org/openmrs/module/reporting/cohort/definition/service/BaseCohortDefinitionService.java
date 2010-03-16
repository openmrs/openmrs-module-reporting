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
package org.openmrs.module.reporting.cohort.definition.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.history.CohortDefinitionSearchHistory;
import org.openmrs.module.reporting.cohort.definition.persister.CohortDefinitionPersister;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.caching.CachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the CohortDefinitionService API
 */
@Transactional
public class BaseCohortDefinitionService extends BaseDefinitionService<CohortDefinition> implements CohortDefinitionService {
	
	private static Log log = LogFactory.getLog(BaseCohortDefinitionService.class);

	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends CohortDefinition>> getDefinitionTypes() {
		List<Class<? extends CohortDefinition>> ret = new ArrayList<Class<? extends CohortDefinition>>();
		for (CohortDefinitionEvaluator e : HandlerUtil.getHandlersForType(CohortDefinitionEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						ret.add((Class<? extends CohortDefinition>) type);
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
	public <D extends CohortDefinition> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getCohortDefinition(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public CohortDefinition getDefinitionByUuid(String uuid) throws APIException {
		for (CohortDefinitionPersister p : getAllPersisters()) {
			CohortDefinition cd = p.getCohortDefinitionByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public List<CohortDefinition> getAllDefinitions(boolean includeRetired) {
		List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
		for (CohortDefinitionPersister p : getAllPersisters()) {
			ret.addAll(p.getAllCohortDefinitions(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (CohortDefinitionPersister p : getAllPersisters()) {
			i += p.getNumberOfCohortDefinitions(includeRetired);
		}
		return i;
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	public List<CohortDefinition> getDefinitions(String name, boolean exactMatchOnly) {
		List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
		for (CohortDefinitionPersister p : getAllPersisters()) {
			ret.addAll(p.getCohortDefinitions(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends CohortDefinition> D saveDefinition(D definition) throws APIException {
		log.debug("Saving cohort definition: " + definition + " of type " + definition.getClass());
		return (D)getPersister(definition.getClass()).saveCohortDefinition(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	public void purgeDefinition(CohortDefinition definition) {
		getPersister(definition.getClass()).purgeCohortDefinition(definition);
	}

	/**
	 * 	This is the main method which should be used to evaluate a CohortDefinition
	 *  - retrieves all evaluation parameter values from the class and the EvaluationContext
	 *  - checks whether a cohort with this configuration exists in the cache (if caching is supported)
	 *  - returns the cached cohort if found
	 *  - otherwise, delegates to the appropriate CohortDefinitionEvaluator and evaluates the result
	 *  - caches the result (if caching is supported)
	 * 
	 * Implementing classes should override the evaluateCohort(EvaluationContext) method
	 * @see getCacheKey(EvaluationContext)
     * @see CohortDefinitionEvaluator#evaluate(EvaluationContext)
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext context) throws APIException {
		
		// Retrieve CohortDefinitionEvaluator which can evaluate this CohortDefinition
		CohortDefinitionEvaluator evaluator = HandlerUtil.getPreferredHandler(CohortDefinitionEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No CohortDefinitionEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}

		// Clone CohortDefinition and set all properties from the Parameters in the EvaluationContext
		CohortDefinition clonedDefinition = DefinitionUtil.clone(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (context != null && context.containsParameter(p.getName())) {
				value = context.getParameterValue(p.getName());
			}
			ReflectionUtil.setPropertyValue(clonedDefinition, p.getName(), value);
		}
		
		// Retrieve from cache if possible, otherwise evaluate
		Cohort c = null;
		if (context != null) {
			Caching caching = clonedDefinition.getClass().getAnnotation(Caching.class);
			if (caching != null && caching.strategy() != NoCachingStrategy.class) {
				try {
					CachingStrategy strategy = caching.strategy().newInstance();
					String cacheKey = strategy.getCacheKey(clonedDefinition);
					if (cacheKey != null) {
						c = (Cohort) context.getFromCache(cacheKey);
					}
					if (c == null) {
						c = evaluator.evaluate(clonedDefinition, context);
						context.addToCache(cacheKey, c);
					}
				}
				catch (Exception e) {
					log.warn("An error occurred while attempting to access the cache.", e);
				}
			}
		}
		if (c == null) {
			c = evaluator.evaluate(clonedDefinition, context);
		}
		if (context != null && context.getBaseCohort() != null && c != null) {
			c = Cohort.intersect(c, context.getBaseCohort());
		}
		
		return new EvaluatedCohort(c, clonedDefinition, context);
	}

	/**
	 * @see BaseDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext context) throws APIException {
		return (EvaluatedCohort)super.evaluate(definition, context);
	}

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
	 * @return all CohortDefinitionPersisters
	 */
	protected List<CohortDefinitionPersister> getAllPersisters() {	
		return HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, null);
	}


	//******* TODO: DO WE REMOVE EVERYTHING BELOW HERE? (MS 3/16/10) ******

	
	private SerializedObjectDAO serializedObjectDAO = null;
	
	private OpenmrsSerializer serializer;
    
    /**
     * @return the serializer
     */
    public OpenmrsSerializer getSerializer() {
    	return serializer;
    }

	
    /**
     * @param serializer the serializer to set
     */
    public void setSerializer(OpenmrsSerializer serializer) {
    	this.serializer = serializer;
    }
	
	
    /**
     * @return the serializedObjectDAO
     */
    public SerializedObjectDAO getSerializedObjectDAO() {
    	return serializedObjectDAO;
    }

	
    /**
     * @param serializedObjectDAO the serializedObjectDAO to set
     */
    public void setSerializedObjectDAO(SerializedObjectDAO serializedObjectDao) {
    	this.serializedObjectDAO = serializedObjectDao;
    }



	/**
	 * @see org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService#clearCurrentUsersCohortDefinitionSearchHistory()
	 */
	public void clearCurrentUsersCohortDefinitionSearchHistory() throws APIException {
		String name = getNameForPersistedSearchHistory(Context.getAuthenticatedUser());
	    List<CohortDefinitionSearchHistory> list = serializedObjectDAO.getAllObjectsByName(CohortDefinitionSearchHistory.class, name, true);
	    if (list != null && list.size() > 0) {
	    	for (CohortDefinitionSearchHistory h : list) {
	    		serializedObjectDAO.purgeObject(h.getId());
	    	}
	    }
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService#getCurrentUsersCohortDefinitionSearchHistory()
	 */
	public CohortDefinitionSearchHistory getCurrentUsersCohortDefinitionSearchHistory() throws APIException {
	    return getPersistedSearchHistory(Context.getAuthenticatedUser());
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService#setCurrentUsersCohortDefinitionSearchHistory(org.openmrs.module.reporting.cohort.definition.history.CohortDefinitionSearchHistory)
	 */
	public void setCurrentUsersCohortDefinitionSearchHistory(CohortDefinitionSearchHistory history) throws APIException {
		String name = getNameForPersistedSearchHistory(Context.getAuthenticatedUser());
		history.setName(name);
		serializedObjectDAO.saveObject(history, serializer);
    }
	
	private CohortDefinitionSearchHistory getPersistedSearchHistory(User user) {
		String name = getNameForPersistedSearchHistory(user);
	    List<CohortDefinitionSearchHistory> ret = serializedObjectDAO.getAllObjectsByName(CohortDefinitionSearchHistory.class, name, true);
	    if (ret == null || ret.size() == 0)
	    	return null;
	    if (ret.size() > 1)
	    	log.warn("Multiple copies (" + ret.size() + ") of " + name);
	    return ret.get(0);
    }

	private String getNameForPersistedSearchHistory(User user) {
	    return "SYSTEM - Search History for " + user.getUsername();
    }
}
