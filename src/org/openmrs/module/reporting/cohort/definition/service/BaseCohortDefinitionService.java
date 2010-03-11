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
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.history.CohortDefinitionSearchHistory;
import org.openmrs.module.reporting.cohort.definition.persister.CohortDefinitionPersister;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.caching.CachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.util.ReflectionUtil;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 *  Base Implementation of the CohortDefinitionService API
 */
@Transactional
public class BaseCohortDefinitionService extends BaseOpenmrsService implements CohortDefinitionService {
	
	private static Log log = LogFactory.getLog(BaseCohortDefinitionService.class);
	
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
	 * @see CohortDefinitionService#getCohortDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends CohortDefinition>> getCohortDefinitionTypes() {
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
	 * Returns either a saved CohortDefinition with the passed uuid, or a new CohortDefinition of the passed type
	 */
    public CohortDefinition getCohortDefinition(String uuid, Class<? extends CohortDefinition> type) {
    	CohortDefinition cd = null;
    	if (StringUtils.hasText(uuid)) {
	    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
	    	cd = cds.getCohortDefinitionByUuid(uuid);
    	}
    	else if (type != null) {
     		try {
    			cd = type.newInstance();
    		}
    		catch (Exception e) {
    			log.error("Exception occurred while instantiating cohort definition of type " + type, e);
    			throw new IllegalArgumentException("Unable to instantiate a CohortDefinition of type: " + type, e);
    		}
    	}
    	else {
    		throw new IllegalArgumentException("You must supply either a uuid or a type");
    	}
    	return cd;
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
	 * @see CohortDefinitionService#getNumberOfCohortDefinitions(boolean)
	 */
	public int getNumberOfCohortDefinitions(boolean includeRetired) {
		int i = 0;
		for (CohortDefinitionPersister p : HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, null)) {
			i += p.getNumberOfCohortDefinitions(includeRetired);
		}
		return i;
	}

	/** 
	 * @see CohortDefinitionService#getCohortDefinitionByName(String, boolean)
	 */
	public List<CohortDefinition> getCohortDefinitions(String name, boolean exactMatchOnly) {
		List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
		for (CohortDefinitionPersister p : HandlerUtil.getHandlersForType(CohortDefinitionPersister.class, null)) {
			ret.addAll(p.getCohortDefinitions(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * TODO: Implement tags. Currently this returns all CohortDefinitions with names that contain the passed tag name.
	 * @see CohortDefinitionService#getCohortDefinitionsByTag(String)
	 */
	public List<CohortDefinition> getCohortDefinitionsByTag(String tagName) {
		return getCohortDefinitions(tagName, false);
	}

	/**
	 * @see CohortDefinitionService#saveCohortDefinition(CohortDefinition)
	 */
	@Transactional
	public CohortDefinition saveCohortDefinition(CohortDefinition definition) throws APIException {		
		log.debug("Saving cohort definition: " + definition + " of type " + definition.getClass());
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
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext evalContext) throws APIException {
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
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext evalContext) throws APIException {
		
		// Retrieve CohortDefinitionEvaluator which can evaluate this CohortDefinition
		CohortDefinitionEvaluator evaluator = HandlerUtil.getPreferredHandler(CohortDefinitionEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No CohortDefinitionEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}

		// Clone CohortDefinition and set all properties from the Parameters in the EvaluationContext
		CohortDefinition clonedDefinition = DefinitionUtil.clone(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (evalContext != null && evalContext.containsParameter(p.getName())) {
				value = evalContext.getParameterValue(p.getName());
			}
			ReflectionUtil.setPropertyValue(clonedDefinition, p.getName(), value);
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
		
		return new EvaluatedCohort(c, clonedDefinition, evalContext);
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
