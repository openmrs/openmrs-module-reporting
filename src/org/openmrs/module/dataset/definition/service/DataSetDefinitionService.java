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

import java.util.List;
import java.util.UUID;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 *  DataSetService
 */
@Transactional
public interface DataSetDefinitionService extends OpenmrsService {

	/**
	 * Evaluate a DataSetDefinition to get turn it into a DataSet
	 * 
	 * @param definition
	 * @param inputCohort Input cohort optionally specified by the user.
	 * @param evalContext EvaluationContext containing parameter values, etc
	 * @return a DataSet matching the parameters
	 * @throws APIException
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext evalContext) throws APIException;
	
	/**
	 * Evaluate a Mapped<DataSetDefinition> to get turn it into a DataSet
	 * 
	 * @param definition
	 * @param inputCohort Input cohort optionally specified by the user.
	 * @param evalContext EvaluationContext containing parameter values, etc
	 * @return a DataSet matching the parameters
	 * @throws APIException
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext evalContext) throws APIException;
	
	/**
     * 
     */
    public List<DataSetDefinition> getAllDataSetDefinitions() throws APIException;

	/**
     * 
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) throws APIException;    

    /**
     * 
     */
    public List<DataSetDefinition> getDatasetDefinitionsByName(String name) throws APIException;

	/**
     * 
     */
    public DataSetDefinition getDataSetDefinitionsByExample(DataSetDefinition definition) throws APIException;    
    
	/**
     * 
     */
    public DataSetDefinition getDataSetDefinition(UUID uuid) throws APIException;
    
    /**
     * TODO Need to deprecate this method since we might have two 
     * dataset definitions with the same ID.
     */
    public DataSetDefinition getDataSetDefinition(Integer id) throws APIException;

    /**
	 * 
     */
    public void purgeDataSetDefinition(DataSetDefinition definition) throws APIException;
    
    /**
	 * 
     */
    public DataSetDefinition saveDatasetDefinition(DataSetDefinition datasetDefinition);    
}

