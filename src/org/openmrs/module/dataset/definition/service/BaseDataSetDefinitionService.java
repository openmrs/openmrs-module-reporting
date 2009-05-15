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
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.dataset.definition.persister.DataExportDataSetDefinitionPersister;
import org.openmrs.module.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the data set service.
 */
@Transactional
public class BaseDataSetDefinitionService extends BaseOpenmrsService implements DataSetDefinitionService {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DataSetDefinitionService#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext evalContext) throws APIException {
		EvaluationContext childContext = EvaluationContext.cloneForChild(evalContext, definition);
		return evaluate(definition.getParameterizable(), childContext);
	}
	
	// ============================================== Persister Methods ==
    	
	
	private List<DataSetDefinitionPersister> getDataSetDefinitionPersisters() { 
		// TODO Need to support 
		//return HandlerUtil.getHandlersForType(DataSetDefinitionPersister.class);
		List<DataSetDefinitionPersister> persisters = 
			new ArrayList<DataSetDefinitionPersister>();
		
		persisters.add(new DataExportDataSetDefinitionPersister());
		
		return persisters;
	}
	
	
	/**
     * @see org.openmrs.module.dataset.definition.service.DataSetDefinitionService#getAllDataSetDefinitions()
     */
    public List<DataSetDefinition> getAllDataSetDefinitions() throws APIException {
    	return getAllDataSetDefinitions(false);
    }

	/**
     * @see org.openmrs.module.dataset.definition.service.DataSetDefinitionService#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
		List<DataSetDefinition> definitions = new ArrayList<DataSetDefinition>();
				
		for (DataSetDefinitionPersister persister : getDataSetDefinitionPersisters()) {
			definitions.addAll(persister.getAllDataSetDefinitions());
		}
		return definitions;
    }
     
    
    
	/**
     * @see org.openmrs.api.DataSetService#getDataSetDefinition(java.util.UUID)
     */
    public DataSetDefinition getDataSetDefinition(UUID uuid) {
    	DataSetDefinition definition = null;
    	for (DataSetDefinitionPersister persister : getDataSetDefinitionPersisters()) {
			definition = persister.getDataSetDefinition(uuid);	
			if (definition != null)
				return definition;
		}
		return definition;    	
    	
    }

	/**
     * @see org.openmrs.api.DataSetService#getDatasetDefinitionByName(java.lang.String)
     */
    public DataSetDefinition getDatasetDefinitionByName(String name) {
    	DataSetDefinition definition = null;
		for (DataSetDefinitionPersister persister : getDataSetDefinitionPersisters()) {			
			definition = 
				persister.getDataSetDefinitionByName(name);
		}
		return definition;    	
    }

	/**
     * @see org.openmrs.api.DataSetService#purgeDataSetDefinition(org.openmrs.module.dataset.definition.DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition definition) {
	    // TODO Auto-generated method stub
    	
    
	    
    }

    public DataSetDefinition saveDatasetDefinition(DataSetDefinition datasetDefinition) {
	    // TODO Auto-generated method stub
	    return null;
    }

    
    public DataSetDefinitionPersister getDataSetDefinitionPersister(Class<DataSetDefinition> clazz) { 
    	for (DataSetDefinitionPersister persister : getDataSetDefinitionPersisters()) {     		
    		if (persister.canPersist(clazz)) { 
    			return persister;
    		}
    	}
    	return null;
    }


	/**
     * @see org.openmrs.module.dataset.definition.service.DataSetDefinitionService#getDataSetDefinition(java.lang.Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {    	
    	DataSetDefinition definition = null;
		for (DataSetDefinitionPersister persister : getDataSetDefinitionPersisters()) {
			definition = persister.getDataSetDefinition(id);	
			if (definition != null)
				return definition;
		}
		return definition;    	
    	
    	
    }
	public DataSetDefinition getDataSetDefinitionsByExample(
			DataSetDefinition definition) throws APIException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DataSetDefinition> getDatasetDefinitionsByName(String name)
			throws APIException {
		// TODO Auto-generated method stub
		return null;
	}	
}
