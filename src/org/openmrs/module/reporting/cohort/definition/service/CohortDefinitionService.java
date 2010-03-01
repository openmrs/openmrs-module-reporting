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

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.history.CohortDefinitionSearchHistory;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate CohortDefinitions
 */
@Transactional
public interface CohortDefinitionService extends OpenmrsService {
	
	/**
	 * @return a List of all CohortDefinition classes that are available for evaluation
	 */
	public List<Class<? extends CohortDefinition>> getCohortDefinitionTypes();
	
	/**
	 * @param type the Class<CohortDefinition> to retrieve
	 * @param id the id to retrieve for the given type
	 * @return the CohortDefinition that matches the given type and id
	 */
	@Transactional(readOnly = true)
	public <T extends CohortDefinition> T getCohortDefinition(Class<T> type, Integer id) throws APIException;
	
	/**
	 * @param uuid
	 * @return the CohortDefinition with the given uuid
	 */
	@Transactional(readOnly = true)
	public CohortDefinition getCohortDefinitionByUuid(String uuid) throws APIException;
	
	
    /**
     * Helper method which checks that either uuid or type is passed, and returns either the
     * saved CohortDefinition with the passed uuid, or a new instance of the CohortDefinition
     * represented by the passed type.  Throws an IllegalArgumentException if any of this is invalid.
     * 
     * @param uuid	
     * @param type
     * @return the CohortDefinition with the given uuid and type
     */
	@Transactional(readOnly = true)
	public CohortDefinition getCohortDefinition(String uuid, Class<? extends CohortDefinition> type);
	
	/**
	 * @param includeRetired - if true, include retired {@link CohortDefinition} in the returned list
	 * @return All {@link CohortDefinition} whose persistence is managed by this persister
	 */
	@Transactional(readOnly = true)
	public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired CohortDefinitions in the count
	 * @return the number of saved Cohort Definitions
	 */
	@Transactional(readOnly = true)
	public int getNumberOfCohortDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link CohortDefinition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<CohortDefinition> objects whose name contains the passed name
	 */
	@Transactional(readOnly = true)
    public List<CohortDefinition> getCohortDefinitions(String name, boolean exactMatchOnly);

	/**
	 * Persists a CohortDefinition, either as a save or update.
	 * @param cohortDefinition
	 * @return the CohortDefinition that was passed in
	 */
	@Transactional
	public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) throws APIException;
	
	/**
	 * Deletes a CohortDefinition from the database.
	 * @param cohortDefinition the CohortDefinition to purge
	 */
	@Transactional
	public void purgeCohortDefinition(CohortDefinition cohortDefinition);
	
	/**
	 * Computes the list of patients who currently meet the given definition<br/>
	 * @param cohortDefinition Mapped<CohortDefinition> to evaluate
	 * @param context context to use during evaluation
	 * @return the cohort of all patients who meet the definition now
	 */
	@Transactional(readOnly = true)
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext context) throws APIException;
	
	/**
	 * Computes the list of patients who currently meet the given definition<br/>
	 * @param cohortDefinition CohortDefinition to evaluate
	 * @param context context to use during evaluation
	 * @return the cohort of all patients who meet the definition now
	 */
	@Transactional(readOnly = true)
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext context) throws APIException;

	/**
	 * Gets the current user's CohortDefinitionSearchHistory, or null if none exists yet  
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public CohortDefinitionSearchHistory getCurrentUsersCohortDefinitionSearchHistory() throws APIException;
	
	/**
	 * Sets the current user's CohortDefinitionSearchHistory 
	 * @param history
	 * @throws APIException
	 */
	@Transactional
	public void setCurrentUsersCohortDefinitionSearchHistory(CohortDefinitionSearchHistory history) throws APIException;
	
	/**
	 * Removes the current user's search history from persisted storage
	 * 
	 * @throws APIException
	 */
	public void clearCurrentUsersCohortDefinitionSearchHistory() throws APIException;
	
}
