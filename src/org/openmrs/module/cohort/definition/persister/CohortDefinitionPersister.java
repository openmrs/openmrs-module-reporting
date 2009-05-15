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

import org.openmrs.module.cohort.definition.CohortDefinition;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of CohortDefinition implementations
 */
public interface CohortDefinitionPersister {
	
	/**
	 * @param includeRetired - if true, include retired CohortDefinitions in the returned list
	 * @return All cohort definitions whose persistence is managed by this persister
	 */
	public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired);
	
	/**
	 * @param id
	 * @return the cohort definition with the given id among those managed by this persister
	 */
	public CohortDefinition getCohortDefinition(Integer id);
	
	/**
	 * @param name
	 * @return the cohort definition with the given name among those managed by this persister
	 */
	public CohortDefinition getCohortDefinitionByName(String name);
	
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
