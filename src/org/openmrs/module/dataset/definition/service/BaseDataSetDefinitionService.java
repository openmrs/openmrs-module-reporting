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
package org.openmrs.module.dataset.definition.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.util.HandlerUtil;
import org.simpleframework.xml.load.Persister;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the DataSetDefinitionService.
 */
@Transactional
public class BaseDataSetDefinitionService extends BaseOpenmrsService implements DataSetDefinitionService {

	/** Logger */
	protected Log log = LogFactory.getLog(this.getClass());
		
    /**
     * 
     */
    public List<Class<? extends DataSetDefinition>> getDataSetDefinitionTypes() { 
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
	 * @see DataSetDefinitionService#getDataSetDefinition(Class, Integer)
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataSetDefinition> T getDataSetDefinition(Class<T> type, Integer id) throws APIException {		
		DataSetDefinitionPersister persister = getPersister(type);
		log.info("Persister: " + persister.getClass().getName());
		return (T) persister.getDataSetDefinition(id);
	}

	/** 
	 * @see DataSetDefinitionService#getDataSetDefinitionByUuid(String)
	 */
	public DataSetDefinition getDataSetDefinitionByUuid(String uuid) throws APIException {
		for (DataSetDefinitionPersister p : HandlerUtil.getHandlersForType(DataSetDefinitionPersister.class, null)) {
			DataSetDefinition datasetDefinition = p.getDataSetDefinitionByUuid(uuid);
			if (datasetDefinition != null) {
				return datasetDefinition;
			}
		}
		return null;
	}

	/**
	 * @see DataSetDefinitionService#getDataSetDefinition(String, Class<? extends DataSetDefinition>)
	 */
    public DataSetDefinition getDataSetDefinition(String uuid, Class<? extends DataSetDefinition> type) {
    	DataSetDefinition datasetDefinition = null;
    	if (StringUtils.hasText(uuid)) {
    		DataSetDefinitionService cds = Context.getService(DataSetDefinitionService.class);
    		datasetDefinition = cds.getDataSetDefinitionByUuid(uuid);
    	}
    	else if (type != null) {
     		try {
     			datasetDefinition = type.newInstance();
    		}
    		catch (Exception e) {
    			throw new IllegalArgumentException("Unable to instantiate a DataSetDefinition of type: " + type);
    		}
    	}
    	else {
    		throw new IllegalArgumentException("You must supply either a uuid or a type");
    	}
    	return datasetDefinition;
    }		
	
	
	/** 
	 * @see DataSetDefinitionService#getAllDataSetDefinitions(boolean)
	 */
	public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
		List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();
		for (DataSetDefinitionPersister persister : HandlerUtil.getHandlersForType(DataSetDefinitionPersister.class, null)) {
			log.info("Persister: " + persister.getClass().getName());			
			if (persister != null) { 
				ret.addAll(persister.getAllDataSetDefinitions(includeRetired));
			}
		}
		return ret;
	}

	/** 
	 * @see DataSetDefinitionService#getDataSetDefinitionByName(String, boolean)
	 */
	public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly) {
		List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();
		for (DataSetDefinitionPersister p : HandlerUtil.getHandlersForType(DataSetDefinitionPersister.class, null)) {
			ret.addAll(p.getDataSetDefinitions(name, exactMatchOnly));
		}
		return ret;
	}

	/**
	 * @see DataSetDefinitionService#saveDataSetDefinition(DataSetDefinition)
	 */
	public DataSetDefinition saveDataSetDefinition(DataSetDefinition definition) throws APIException {
		return getPersister(definition.getClass()).saveDataSetDefinition(definition);
	}

	/** 
	 * @see DataSetDefinitionService#purgeDataSetDefinition(DataSetDefinition)
	 */
	public void purgeDataSetDefinition(DataSetDefinition definition) {
		getPersister(definition.getClass()).purgeDataSetDefinition(definition);
	}
	
	/**
	 * @see DataSetDefinitionService#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition definition, EvaluationContext context) throws APIException {
		DataSetEvaluator evaluator = HandlerUtil.getPreferredHandler(DataSetEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No DataSetEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}
		return evaluator.evaluate(definition, context);
	}
	
	/** 
	 * @see DataSetDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public DataSet<?> evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext evalContext) throws APIException {
		EvaluationContext childContext = EvaluationContext.cloneForChild(evalContext, definition);
		return evaluate(definition.getParameterizable(), childContext);
	}
}
