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
package org.openmrs.module.cohort.definition.persister;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.springframework.transaction.annotation.Transactional;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of CohortDefinition implementations
 */
//@Transactional
public interface CohortDefinitionPersister {
	
	/**
	 * @param id
	 * @return the cohort definition with the given id among those managed by this persister
	 */
	public CohortDefinition getCohortDefinition(Integer id);
	
	/**
	 * @param uuid
	 * @return the cohort definition with the given uuid among those managed by this persister
	 */
	public CohortDefinition getCohortDefinitionByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired CohortDefinitions in the returned list
	 * @return All cohort definitions whose persistence is managed by this persister
	 */
	public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link CohortDefinition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<CohortDefinition> objects whose name contains the passed name
	 */
	public List<CohortDefinition> getCohortDefinitions(String name, boolean exactMatchOnly) throws APIException;
	
	/**
	 * Persists a CohortDefinition, either as a save or update.
	 * @param cohortDefinition
	 * @return the CohortDefinition that was passed in
	 */
	public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition);
	
	/**
	 * Deletes a cohort definition from the database.
	 * @param cohortDefinition
	 */
	public void purgeCohortDefinition(CohortDefinition cohortDefinition);
}
