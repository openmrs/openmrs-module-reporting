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
package org.openmrs.module.reporting.dataset.definition.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the DataSetDefinitionService.
 */
@Transactional
public class BaseDataSetDefinitionService extends BaseDefinitionService<DataSetDefinition> implements DataSetDefinitionService {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends DataSetDefinition>> getDefinitionTypes() {
		List<Class<? extends DataSetDefinition>> ret = new ArrayList<Class<? extends DataSetDefinition>>();
		for (DataSetEvaluator e : HandlerUtil.getHandlersForType(DataSetEvaluator.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {
						ret.add((Class<? extends DataSetDefinition>) type);
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
	public <D extends DataSetDefinition> D getDefinition(Class<D> type, Integer id) throws APIException {
		return (D) getPersister(type).getDataSetDefinition(id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public DataSetDefinition getDefinitionByUuid(String uuid) throws APIException {
		for (DataSetDefinitionPersister p : getAllPersisters()) {
			DataSetDefinition cd = p.getDataSetDefinitionByUuid(uuid);
			if (cd != null) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public List<DataSetDefinition> getAllDefinitions(boolean includeRetired) {
		List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();
		for (DataSetDefinitionPersister p : getAllPersisters()) {
			ret.addAll(p.getAllDataSetDefinitions(includeRetired));
		}
		return ret;
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		int i = 0;
		for (DataSetDefinitionPersister p : getAllPersisters()) {
			i += p.getNumberOfDataSetDefinitions(includeRetired);
		}
		return i;
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	public List<DataSetDefinition> getDefinitions(String name, boolean exactMatchOnly) {
		List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();
		for (DataSetDefinitionPersister p : getAllPersisters()) {
			ret.addAll(p.getDataSetDefinitions(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public <D extends DataSetDefinition> D saveDefinition(D definition) throws APIException {
		log.debug("Saving cohort definition: " + definition + " of type " + definition.getClass());
		return (D)getPersister(definition.getClass()).saveDataSetDefinition(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 */
	public void purgeDefinition(DataSetDefinition definition) {
		getPersister(definition.getClass()).purgeDataSetDefinition(definition);
	}
	
	/**
	 * @see DataSetDefinitionService#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext context) throws APIException {
		DataSetEvaluator evaluator = HandlerUtil.getPreferredHandler(DataSetEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No DataSetEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}
		return evaluator.evaluate(definition, context);
	}
	
	/** 
	 * @see DataSetDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext context) throws APIException {
		return (DataSet) super.evaluate(definition, context);
	}
	
	/**
	 * Returns the DataSetDefinitionPersister for the passed DataSetDefinition
	 * @param definition
	 * @return the DataSetDefinitionPersister for the passed DataSetDefinition
	 * @throws APIException if no matching persister is found
	 */
	protected DataSetDefinitionPersister getPersister(Class<? extends DataSetDefinition> definition) {
		DataSetDefinitionPersister persister = HandlerUtil.getPreferredHandler(DataSetDefinitionPersister.class, definition);
		if (persister == null) {
			throw new APIException("No DataSetDefinitionPersister found for <" + definition + ">");
		}
		return persister;
	}
	
	/**
	 * @return all DataSetDefinitionPersisters
	 */
	protected List<DataSetDefinitionPersister> getAllPersisters() {	
		return HandlerUtil.getHandlersForType(DataSetDefinitionPersister.class, null);
	}
}
