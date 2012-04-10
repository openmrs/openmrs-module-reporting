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
package org.openmrs.module.reporting.definition.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.DuplicateTagException;
import org.openmrs.module.reporting.cohort.definition.DefinitionTag;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.db.DefinitionDAO;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.definition.persister.DefinitionPersister;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.util.HandlerUtil;
import org.openmrs.validator.ValidateUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Base Implementation of the DefinitionService API. Note that any subclasses that want
 * {@link #getAllDefinitionSummaries(boolean)} to perform well need to override it.
 */
@Transactional
public abstract class BaseDefinitionService<T extends Definition> extends BaseOpenmrsService implements DefinitionService<T> {
	
	protected static Log log = LogFactory.getLog(BaseDefinitionService.class);
	
	private DefinitionDAO dao;
	
	/**
	 * @return the dao
	 */
	protected DefinitionDAO getDao() {
		return dao;
	}
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(DefinitionDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends T>> getDefinitionTypes() {
		List<Class<? extends T>> ret = new ArrayList<Class<? extends T>>();
		for (DefinitionEvaluator<?> e : HandlerUtil.getHandlersForType(DefinitionEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						if (getDefinitionType().isAssignableFrom(type)) {
							ret.add((Class<? extends T>) type);
						}
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
	public <D extends T> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getDefinition(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	@SuppressWarnings("unchecked")
	public T getDefinitionByUuid(String uuid) throws APIException {
		for (DefinitionPersister<?> p : getAllPersisters()) {
			T d = (T) p.getDefinitionByUuid(uuid);
			if (d != null) {
				return d;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<T> getAllDefinitions(boolean includeRetired) {
		List<T> ret = new ArrayList<T>();
		for (DefinitionPersister<?> p : getAllPersisters()) {
			ret.addAll((List<T>) p.getAllDefinitions(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (DefinitionPersister<?> p : getAllPersisters()) {
			i += p.getNumberOfDefinitions(includeRetired);
		}
		return i;
	}
	
	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<T> getDefinitions(String name, boolean exactMatchOnly) {
		List<T> ret = new ArrayList<T>();
		for (DefinitionPersister<?> p : getAllPersisters()) {
			ret.addAll((List<T>) p.getDefinitions(name, exactMatchOnly));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends T> D saveDefinition(D definition) throws APIException {
		log.debug("Saving definition: " + definition + " of type " + definition.getClass());
		return (D) getPersister((Class<T>) definition.getClass()).saveDefinition(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	@SuppressWarnings("unchecked")
	public void purgeDefinition(T definition) {
		getPersister((Class<T>) definition.getClass()).purgeDefinition(definition);
	}
	
	/**
	 * @see DefinitionService#getDefinition(String, Class)
	 */
	@Transactional(readOnly = true)
	public T getDefinition(String uuid, Class<? extends T> type) {
		T ret = null;
		if (StringUtils.hasText(uuid)) {
			ret = getDefinitionByUuid(uuid);
		}
		if (ret == null) {
			if (type != null) {
				try {
					ret = type.newInstance();
				}
				catch (Exception e) {
					log.error("Exception occurred while instantiating definition of type " + type, e);
					throw new IllegalArgumentException("Unable to instantiate a Definition of type: " + type, e);
				}
			} else {
				throw new IllegalArgumentException("You must supply either a uuid or a type");
			}
		}
		return ret;
	}
	
	/**
	 * Note that this base implementation is no more efficient than just calling
	 * {@link #getAllDefinitions(boolean)} it should really be overridden in any subclasses that
	 * intend to use this functionality.
	 * 
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getAllDefinitionSummaries(boolean)
	 */
	public List<DefinitionSummary> getAllDefinitionSummaries(boolean includeRetired) {
		List<DefinitionSummary> ret = new ArrayList<DefinitionSummary>();
		for (T def : getAllDefinitions(includeRetired)) {
			ret.add(new DefinitionSummary(def));
		}
		return ret;
	}
	
	/**
	 * This is the main method which should be used to evaluate a Definition - retrieves all
	 * evaluation parameter values from the class and the EvaluationContext - checks whether a
	 * definition with this configuration exists in the cache (if caching is supported) - returns
	 * the cached evaluation result if found - otherwise, delegates to the appropriate Evaluator and
	 * evaluates the result - caches the result (if caching is supported)
	 * 
	 * @see getCacheKey(EvaluationContext)
	 * @see DefinitionEvaluator#evaluate(Definition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public Evaluated<T> evaluate(T definition, EvaluationContext context) throws EvaluationException {
		
		// Ensure context is not null
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		// Retrieve QueryEvaluator which can evaluate this Query
		DefinitionEvaluator<T> evaluator = DefinitionUtil.getPreferredEvaluator(definition);
		
		// Clone Query and set all properties from the Parameters in the EvaluationContext
		T clonedDefinition = DefinitionUtil.cloneDefinitionWithContext(definition, context);
		
		String cacheKey = EvaluationUtil.getCacheKey(clonedDefinition);
		
		// Retrieve from cache if possible, otherwise evaluate
		Evaluated<T> evaluationResult = null;
		if (cacheKey != null) {
			evaluationResult = (Evaluated<T>) context.getFromCache(cacheKey);
			if (evaluationResult == null) {
				evaluationResult = evaluator.evaluate(clonedDefinition, context);
				context.addToCache(cacheKey, evaluationResult);
			}
		}
		
		if (evaluationResult == null) {
			evaluationResult = evaluator.evaluate(clonedDefinition, context);
		}
		
		return evaluationResult;
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) throws EvaluationException {
		if (definition == null || definition.getParameterizable() == null) {
			throw new MissingDependencyException();
		}
		EvaluationContext childContext = EvaluationContext.cloneForChild(context, definition);
		log.debug("Evaluating: " + definition.getParameterizable() + "(" + context.getParameterValues() + ")");
		return evaluate(definition.getParameterizable(), childContext);
	}
	
	/**
	 * @return the DefinitionPersister<?> for the passed Definition
	 * @throws APIException if no matching persister is found
	 */
	@SuppressWarnings("unchecked")
	protected DefinitionPersister<T> getPersister(Class<? extends T> definition) {
		DefinitionPersister<T> persister = HandlerUtil.getPreferredHandler(DefinitionPersister.class, definition);
		if (persister == null) {
			throw new APIException("No Persister found for <" + definition + ">");
		}
		return persister;
	}
	
	/**
	 * @return all DefinitionPersister<?>s
	 */
	@SuppressWarnings("rawtypes")
	protected List<DefinitionPersister> getAllPersisters() {
		return HandlerUtil.getHandlersForType(DefinitionPersister.class, getDefinitionType());
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getAllTags()
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getAllTags() {
		//Since we are still going to loop over the tags, to get the tag names, get all
		//so that the loop is only run once
		List<DefinitionTag> definitionTags = getAllDefinitionTags();
		Set<String> tags = new HashSet<String>(definitionTags.size());
		for (DefinitionTag definitionTag : definitionTags) {
			Class<T> clazz = (Class<T>) ReportUtil.loadClass(definitionTag.getDefinitionType());
			//Filter on only this service's actual type's subclasses
			if (getDefinitionType().isAssignableFrom(clazz))
				tags.add(definitionTag.getTag());
		}
		
		return tags;
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getAllDefinitionsHavingTag(String)
	 */
	@SuppressWarnings("unchecked")
	public List<DefinitionSummary> getAllDefinitionsHavingTag(String tag) {
		if (!StringUtils.hasText(tag))
			throw new IllegalArgumentException("tag cannot be null or blank");
		
		List<DefinitionTag> definitionTags = dao.getDefinitionTags(tag, null);
		List<DefinitionSummary> defSummaries = new ArrayList<DefinitionSummary>();
		for (DefinitionTag definitionTag : definitionTags) {
			Class<T> clazz = (Class<T>) ReportUtil.loadClass(definitionTag.getDefinitionType());
			if (getDefinitionType().isAssignableFrom(clazz)) {
				T definition = getDefinition(definitionTag.getDefinitionUuid(), clazz);
				DefinitionSummary ds = new DefinitionSummary(definition);
				//Why isn't the definition above returned without a uuid?
				ds.setUuid(definitionTag.getDefinitionUuid());
				defSummaries.add(ds);
			}
		}
		
		return defSummaries;
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getAllDefinitionTags()
	 */
	@SuppressWarnings("unchecked")
	public List<DefinitionTag> getAllDefinitionTags() {
		List<DefinitionTag> possibleTags = dao.getDefinitionTags(null, null);
		List<DefinitionTag> dTags = new ArrayList<DefinitionTag>();
		for (DefinitionTag definitionTag : possibleTags) {
			Class<T> type = (Class<T>) ReportUtil.loadClass(definitionTag.getDefinitionType());
			if (getDefinitionType().isAssignableFrom(type)) {
				dTags.add(definitionTag);
			}
		}
		
		return dTags;
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getAllDefinitionsByTag()
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<DefinitionSummary>> getAllDefinitionsByTag() {
		List<DefinitionTag> definitionTags = getAllDefinitionTags();
		Map<String, List<DefinitionSummary>> tagDefSummariesMap = new HashMap<String, List<DefinitionSummary>>();
		for (DefinitionTag definitionTag : definitionTags) {
			Class<T> clazz = (Class<T>) ReportUtil.loadClass(definitionTag.getDefinitionType());
			if (getDefinitionType().isAssignableFrom(clazz)) {
				String tag = definitionTag.getTag();
				if (tagDefSummariesMap.get(tag) == null)
					tagDefSummariesMap.put(tag, new ArrayList<DefinitionSummary>());
				
				T definition = getDefinition(definitionTag.getDefinitionUuid(), clazz);
				DefinitionSummary ds = new DefinitionSummary(definition);
				ds.setUuid(definitionTag.getDefinitionUuid());
				tagDefSummariesMap.get(tag).add(ds);
			}
		}
		
		return tagDefSummariesMap;
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#addTagToDefinition(Definition,
	 *      String)
	 */
	public boolean addTagToDefinition(T definition, String tag) {
		if (definition == null || !StringUtils.hasText(tag))
			throw new IllegalArgumentException("definition or tag cannot be null or blank");
		
		DefinitionTag definitionTag = new DefinitionTag(tag, definition);
		try {
			ValidateUtil.validate(definitionTag);
		}
		catch (DuplicateTagException e) {
			//silently ignore diplicates
			return false;
		}
		//Only 1.9+ sets the uuid on object instantiation and this isn't named saveXX so AOP isn't helping
		if (definitionTag.getUuid() == null)
			definitionTag.setUuid(UUID.randomUUID().toString());
		dao.saveDefinitionTag(definitionTag);
		return true;
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#removeTagFromDefinition(Definition,
	 *      String)
	 */
	public void removeTagFromDefinition(T definition, String tag) {
		if (definition != null)
			dao.deleteDefinitionTag(definition.getUuid(), tag);
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#hasTag(Definition,
	 *      String)
	 */
	public boolean hasTag(T definition, String tag) {
		if (definition == null || definition.getUuid() == null)
			return false;
		
		return hasTag(definition.getUuid(), tag);
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#hasTag(String, String)
	 */
	public boolean hasTag(String definitionUuid, String tag) {
		if (definitionUuid == null || tag == null)
			return false;
		
		return dao.checkIfTagExists(definitionUuid, tag);
	}
	
	/**
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getTags(org.openmrs.module.reporting.evaluation.Definition)
	 */
	public List<String> getTags(T definition) {
		if (definition == null)
			throw new IllegalArgumentException("definition cannot be null");
		
		List<DefinitionTag> definitionTags = dao.getDefinitionTags(null, definition.getUuid());
		List<String> tags = new ArrayList<String>();
		for (DefinitionTag definitionTag : definitionTags) {
			tags.add(definitionTag.getTag());
		}
		return tags;
	}
}
