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
package org.openmrs.module.reporting.indicator.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.evaluator.IndicatorEvaluator;
import org.openmrs.module.reporting.indicator.persister.IndicatorPersister;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Implementation of IndicatorService
 */
@Transactional
public class BaseIndicatorService extends BaseDefinitionService<Indicator> implements IndicatorService {

	protected static Log log = LogFactory.getLog(BaseIndicatorService.class);
	
	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends Indicator>> getDefinitionTypes() {
		List<Class<? extends Indicator>> ret = new ArrayList<Class<? extends Indicator>>();
		for (IndicatorEvaluator e : HandlerUtil.getHandlersForType(IndicatorEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						ret.add((Class<? extends Indicator>) type);
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
	public <D extends Indicator> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getIndicator(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public Indicator getDefinitionByUuid(String uuid) throws APIException {
		for (IndicatorPersister p : getAllPersisters()) {
			Indicator cd = p.getIndicatorByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public List<Indicator> getAllDefinitions(boolean includeRetired) {
		List<Indicator> ret = new ArrayList<Indicator>();
		for (IndicatorPersister p : getAllPersisters()) {
			ret.addAll(p.getAllIndicators(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (IndicatorPersister p : getAllPersisters()) {
			i += p.getNumberOfIndicators(includeRetired);
		}
		return i;
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	public List<Indicator> getDefinitions(String name, boolean exactMatchOnly) {
		List<Indicator> ret = new ArrayList<Indicator>();
		for (IndicatorPersister p : getAllPersisters()) {
			ret.addAll(p.getIndicators(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends Indicator> D saveDefinition(D definition) throws APIException {
		log.debug("Saving cohort definition: " + definition + " of type " + definition.getClass());
		return (D)getPersister(definition.getClass()).saveIndicator(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	public void purgeDefinition(Indicator definition) {
		getPersister(definition.getClass()).purgeIndicator(definition);
	}
	
	/** 
	 * @see IndicatorService#evaluate(Indicator, EvaluationContext)
	 */
	public IndicatorResult evaluate(Indicator definition, EvaluationContext context) {
		IndicatorEvaluator evaluator = HandlerUtil.getPreferredHandler(IndicatorEvaluator.class, definition.getClass());
		return evaluator.evaluate(definition, context);
	}
	
	/** 
	 * @see IndicatorService#evaluate(Mapped, EvaluationContext)
	 */
	public IndicatorResult evaluate(Mapped<? extends Indicator> definition, EvaluationContext context) {
		return (IndicatorResult) super.evaluate(definition, context);
	}
	
	/**
	 * Returns the IndicatorPersister for the passed Indicator
	 * @param definition the Indicator to persist
	 * @return the IndicatorPersister for the passed Indicator
	 * @throws APIException if no matching persister is found
	 */
	protected IndicatorPersister getPersister(Class<? extends Indicator> definition) {
		IndicatorPersister persister = HandlerUtil.getPreferredHandler(IndicatorPersister.class, definition);
		if (persister == null) {
			throw new APIException("No IndicatorPersister found for <" + definition + ">");
		}
		return persister;
	}
	
	/**
	 * @return all IndicatorPersisters
	 */
	protected List<IndicatorPersister> getAllPersisters() {	
		return HandlerUtil.getHandlersForType(IndicatorPersister.class, null);
	}
}
