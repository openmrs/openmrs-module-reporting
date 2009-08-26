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

import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 *  DataSetService
 */
@Transactional
public interface DataSetDefinitionService extends OpenmrsService {
	
	
	/** 
	 * 
	 */
	@Transactional(readOnly = true)
	public List<Class<? extends DataSetDefinition>> getDataSetDefinitionTypes();

	/**
	 * @param type the Class<DataSetDefinition> to retrieve
	 * @param id the id to retrieve for the given type
	 * @return the DataSetDefinition that matches the given type and id
	 */
	@Transactional(readOnly = true)
	public <T extends DataSetDefinition> T getDataSetDefinition(Class<T> type, Integer id) throws APIException;
	
    /**
     * Helper method which checks that either uuid or type is passed, and returns either the
     * saved DataSetDefinition with the passed uuid, or a new instance of the DataSetDefinition
     * represented by the passed type.  Throws an IllegalArgumentException if any of this is invalid.
     * 
     * @param uuid	
     * @param type
     * @return the DataSetDefinition with the given uuid or type
     */
	@Transactional(readOnly = true)
	public DataSetDefinition getDataSetDefinition(String uuid, Class<? extends DataSetDefinition> type);	
	
	/**
	 * @param uuid
	 * @return the DataSetDefinition with the given uuid
	 */
	@Transactional(readOnly = true)
	public DataSetDefinition getDataSetDefinitionByUuid(String uuid) throws APIException;
	
	/**
	 * @param includeRetired - if true, include retired {@link DataSetDefinition} in the returned list
	 * @return All {@link DataSetDefinition} whose persistence is managed by this persister
	 */
	@Transactional(readOnly = true)
	public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link DataSetDefinition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<DataSetDefinition> objects whose name contains the passed name
	 */
	@Transactional(readOnly = true)
    public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly);

	/**
	 * Persists a DataSetDefinition, either as a save or update.
	 * @param DataSetDefinition
	 * @return the DataSetDefinition that was passed in
	 */
	@Transactional
	public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) throws APIException;
	
	/**
	 * Deletes a DataSetDefinition from the database.
	 * @param DataSetDefinition the DataSetDefinition to purge
	 */
	@Transactional
	public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition);

	/**
	 * Evaluate a Mapped<DataSetDefinition> to get turn it into a DataSet
	 * 
	 * @param definition
	 * @param inputCohort Input cohort optionally specified by the user.
	 * @param context EvaluationContext containing parameter values, etc
	 * @return a DataSet matching the parameters
	 * @throws APIException
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext context) throws APIException;
	
	/**
	 * Evaluate a DataSetDefinition to get turn it into a DataSet
	 * 
	 * @param definition
	 * @param inputCohort Input cohort optionally specified by the user.
	 * @param context EvaluationContext containing parameter values, etc
	 * @return a DataSet matching the parameters
	 * @throws APIException
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext context) throws APIException;
}
