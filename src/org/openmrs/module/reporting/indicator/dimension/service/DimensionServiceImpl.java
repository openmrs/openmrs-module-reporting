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
package org.openmrs.module.reporting.indicator.dimension.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.dimension.evaluator.DimensionEvaluator;
import org.openmrs.module.reporting.indicator.dimension.persister.DimensionPersister;
import org.openmrs.util.HandlerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Implementation of DimensionService
 */
@Transactional
@Service
public class DimensionServiceImpl extends BaseDefinitionService<Dimension> implements DimensionService {

	protected static Log log = LogFactory.getLog(DimensionServiceImpl.class);
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<Dimension> getDefinitionType() {
		return Dimension.class;
	}
	
	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends Dimension>> getDefinitionTypes() {
		List<Class<? extends Dimension>> ret = new ArrayList<Class<? extends Dimension>>();
		for (DimensionEvaluator e : HandlerUtil.getHandlersForType(DimensionEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						ret.add((Class<? extends Dimension>) type);
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
	public <D extends Dimension> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getDimension(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public Dimension getDefinitionByUuid(String uuid) throws APIException {
		for (DimensionPersister p : getAllPersisters()) {
			Dimension cd = p.getDimensionByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public List<Dimension> getAllDefinitions(boolean includeRetired) {
		List<Dimension> ret = new ArrayList<Dimension>();
		for (DimensionPersister p : getAllPersisters()) {
			ret.addAll(p.getAllDimensions(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (DimensionPersister p : getAllPersisters()) {
			i += p.getNumberOfDimensions(includeRetired);
		}
		return i;
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	public List<Dimension> getDefinitions(String name, boolean exactMatchOnly) {
		List<Dimension> ret = new ArrayList<Dimension>();
		for (DimensionPersister p : getAllPersisters()) {
			ret.addAll(p.getDimensions(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends Dimension> D saveDefinition(D definition) throws APIException {
		log.debug("Saving: " + definition + " of type " + definition.getClass());
		return (D)getPersister(definition.getClass()).saveDimension(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	public void purgeDefinition(Dimension definition) {
		getPersister(definition.getClass()).purgeDimension(definition);
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	public Evaluated<Dimension> evaluate(Dimension definition, EvaluationContext context) throws APIException {
		DimensionEvaluator evaluator = HandlerUtil.getPreferredHandler(DimensionEvaluator.class, definition.getClass());
		return evaluator.evaluate(definition, context);
	}

	/**
	 * Returns the DimensionPersister for the passed Dimension
	 * @param definition the Dimension to persist
	 * @return the DimensionPersister for the passed Dimension
	 * @throws APIException if no matching persister is found
	 */
	protected DimensionPersister getPersister(Class<? extends Dimension> definition) {
		DimensionPersister persister = HandlerUtil.getPreferredHandler(DimensionPersister.class, definition);
		if (persister == null) {
			throw new APIException("No DimensionPersister found for <" + definition + ">");
		}
		return persister;
	}
	
	/**
	 * @return all DimensionPersisters
	 */
	protected List<DimensionPersister> getAllPersisters() {	
		return HandlerUtil.getHandlersForType(DimensionPersister.class, null);
	}
}
