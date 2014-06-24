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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.definition.persister.DefinitionPersister;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Implementation of the DefinitionService API. Note that any subclasses that want
 * {@link #getAllDefinitionSummaries(boolean)} to perform well need to override it.
 */
public abstract class BaseDefinitionService<T extends Definition> extends BaseOpenmrsService implements DefinitionService<T> {
	
	protected static Log log = LogFactory.getLog(BaseDefinitionService.class);
	
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
	@Transactional(readOnly = true)
	public <D extends T> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getDefinition(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional
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
		if (ObjectUtil.notNull(uuid)) {
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
	 * Default implementation is to consider a Definition to contain a tag if the tag is part of the
	 * Definition name
	 * 
	 * @see DefinitionService#getDefinitionsByTag(String)
	 */
	@Transactional(readOnly = true)
	public List<T> getDefinitionsByTag(String tagName) {
		return getDefinitions(tagName, false);
	}
	
	/**
	 * Note that this base implementation is no more efficient than just calling
	 * {@link #getAllDefinitions(boolean)} it should really be overridden in any subclasses that
	 * intend to use this functionality.
	 * 
	 * @see org.openmrs.module.reporting.definition.service.DefinitionService#getAllDefinitionSummaries(boolean)
	 */
	@Transactional(readOnly = true)
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
	 * @see DefinitionEvaluator#evaluate(Definition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public Evaluated<T> evaluate(T definition, EvaluationContext context) throws EvaluationException {

        context = ObjectUtil.nvl(context, new EvaluationContext());

        // This is a cross-cutting entry point for all evaluations in the reporting module, so doing this here covers
        // all scenarios
        ensureNoTestPatients(context);

        return evaluateBypassingExclusionOfTestPatients(definition, context);
    }

    /**
     * IF YOU ARE A NORMAL CONSUMER OF THE API, DO NOT CALL THIS METHOD! THE CORRECT CONSUMER-FACING METHOD IS:
     * {@link #evaluate(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)}
     *
     * This method performs the logic of the evaluate method, but it bypasses the logic to exclude test patients. This
     * is used internally by the reporting framework in special cases, e.g. to determine <em>which</em> patients are
     * test patients.
     *
     * (This method will only be visible to an API consumer if a service interface explicitly exposes it, and this
     * should only be done for CohortDefinitionService.)
     *
     * @param definition
     * @param context
     * @return
     * @throws EvaluationException
     */
    public Evaluated<T> evaluateBypassingExclusionOfTestPatients(T definition, EvaluationContext context) throws EvaluationException {

        // Retrieve QueryEvaluator which can evaluate this Query
        DefinitionEvaluator<T> evaluator = DefinitionUtil.getPreferredEvaluator(definition);

        // Clone Query and set all properties from the Parameters in the EvaluationContext
        T clonedDefinition = DefinitionUtil.cloneDefinitionWithContext(definition, context);

        String cacheKey = EvaluationUtil.getCacheKey(clonedDefinition, context);

        // Retrieve from cache if possible, otherwise evaluate
        Evaluated<T> evaluationResult = null;
        if (cacheKey != null) {
            evaluationResult = (Evaluated<T>) context.getFromCache(cacheKey);
            if (evaluationResult == null) {
                log.debug("No cached value with key <" + cacheKey + ">.  Evaluating.");
                evaluationResult = executeEvaluator(evaluator, clonedDefinition, context);
                context.addToCache(cacheKey, evaluationResult);
            } else {
                log.debug("Retrieved cached value with key <" + cacheKey + "> = " + evaluationResult);
            }
        }

        if (evaluationResult == null) {
            evaluationResult = executeEvaluator(evaluator, clonedDefinition, context);
        }

        return evaluationResult;
    }

    /**
     * Ensures that if a "test patients" cohort definition is configured, that no test patients are included in this
     * evaluation context.
     * If the context has a baseCohort defined, we remove those patients ids
     * @param context
     */
    private void ensureNoTestPatients(EvaluationContext context) throws EvaluationException {
        CohortDefinition testPatientsDefinition = ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION();
        if (testPatientsDefinition == null) {
            // if no "test patients" cohort definition is configured, we do nothing
            return;
        }
        else {
            CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
            EvaluatedCohort testPatients = cohortDefinitionService.evaluateBypassingExclusionOfTestPatients(testPatientsDefinition, context);
            if (testPatients.size() == 0) {
                // if there are no test patients in the system, we do nothing
                return;
            }

            if (context.getBaseCohort() == null) {
                // set a base cohort of "all patients" minus "test patients"
                EvaluatedCohort allPatients = cohortDefinitionService.evaluateBypassingExclusionOfTestPatients(new AllPatientsCohortDefinition(), context);
                allPatients.getMemberIds().removeAll(testPatients.getMemberIds());
                context.setBaseCohort(allPatients);
            }
            else {
                // remove test patients from the existing baseCohort, and if this results in any changes, clear the cache
                boolean changed = context.getBaseCohort().getMemberIds().removeAll(testPatients.getMemberIds());
                if (changed) {
                    context.clearCache();
                }
            }
        }
    }

    /**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) throws EvaluationException {
		if (definition == null || definition.getParameterizable() == null) {
			throw new MissingDependencyException();
		}
		EvaluationContext childContext = EvaluationContext.cloneForChild(context, definition);
		log.debug("Evaluating: " + definition.getParameterizable() + "(" + context.getParameterValues() + ")");
		return evaluate(definition.getParameterizable(), childContext);
	}

	/**
	 * Subclasses should override this method if they need to insert specific behavior around calling the
	 * actual evaluators.  Examples might include running the evaluators in batches, etc
	 */
	protected Evaluated<T> executeEvaluator(DefinitionEvaluator<T> evaluator, T definition, EvaluationContext context) throws EvaluationException {
		Evaluated<T> ret = null;
		EvaluationProfiler profiler = new EvaluationProfiler(context);
		String formattedDefinition = DefinitionUtil.format(definition);
		if (context.getEvaluationLevel() == 1) {
			profiler.logBefore("EVALUATION_STARTED", Context.getAuthenticatedUser().getDisplayString());
		}
		profiler.logBefore("EVALUATING_DEFINITION", formattedDefinition);
		try {
			ret = evaluator.evaluate(definition, context);
		}
		catch (EvaluationException e) {
			profiler.logError("EVALUATING_DEFINITION", formattedDefinition, e);
			throw e;
		}
		catch (Throwable t) {
			profiler.logError("EVALUATING_DEFINITION", formattedDefinition, t);
			throw new EvaluationException("Error evaluating", t);
		}
		profiler.logAfter("EVALUATING_DEFINITION", formattedDefinition);
		if (context.getEvaluationLevel() == 1) {
			profiler.logAfter("EVALUATION_COMPLETED", profiler.getTimeElapsedFromStart());
			context.clearCache();
		}
		return ret;
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
}
