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
package org.openmrs.module.reporting.idset.persister;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.idset.definition.IdSetDefinition;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of IdSetDefinition implementations
 */
public interface IdSetDefinitionPersister {
	
	/**
	 * @param id
	 * @return the cohort definition with the given id among those managed by this persister
	 */
	public IdSetDefinition getIdSetDefinition(Integer id);
	
	/**
	 * @param uuid
	 * @return the cohort definition with the given uuid among those managed by this persister
	 */
	public IdSetDefinition getIdSetDefinitionByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired IdSetDefinitions in the returned list
	 * @return All cohort definitions whose persistence is managed by this persister
	 */
	public List<IdSetDefinition> getAllIdSetDefinitions(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired IdSetDefinitions in the count
	 * @return the number of saved Cohort Definitions
	 */
	public int getNumberOfIdSetDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link IdSetDefinition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<IdSetDefinition> objects whose name contains the passed name
	 */
	public List<IdSetDefinition> getIdSetDefinitions(String name, boolean exactMatchOnly) throws APIException;
	
	/**
	 * Persists a IdSetDefinition, either as a save or update.
	 * @param IdSetDefinition
	 * @return the IdSetDefinition that was passed in
	 */
	public IdSetDefinition saveIdSetDefinition(IdSetDefinition idSetDefinition);
	
	/**
	 * Deletes a cohort definition from the database.
	 * @param IdSetDefinition
	 */
	public void purgeIdSetDefinition(IdSetDefinition idSetDefinition);
}
