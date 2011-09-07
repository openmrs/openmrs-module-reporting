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
package org.openmrs.module.reporting.query.persister;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.query.definition.Query;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of Query implementations
 */
public interface QueryPersister {
	
	/**
	 * @param id
	 * @return the cohort definition with the given id among those managed by this persister
	 */
	public Query getQuery(Integer id);
	
	/**
	 * @param uuid
	 * @return the cohort definition with the given uuid among those managed by this persister
	 */
	public Query getQueryByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired Querys in the returned list
	 * @return All cohort definitions whose persistence is managed by this persister
	 */
	public List<Query> getAllQuerys(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Querys in the count
	 * @return the number of saved Cohort Definitions
	 */
	public int getNumberOfQuerys(boolean includeRetired);
	
	/**
	 * Returns a List of {@link Query} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<Query> objects whose name contains the passed name
	 */
	public List<Query> getQuerys(String name, boolean exactMatchOnly) throws APIException;
	
	/**
	 * Persists a Query, either as a save or update.
	 * @param Query
	 * @return the Query that was passed in
	 */
	public Query saveQuery(Query queryResultDefinition);
	
	/**
	 * Deletes a cohort definition from the database.
	 * @param Query
	 */
	public void purgeQuery(Query queryResultDefinition);
}
