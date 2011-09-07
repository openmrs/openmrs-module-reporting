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
package org.openmrs.module.reporting.definition.persister;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * This interface provides a means for specific definition implementations to plug in
 * specific implementations for how they should be persisted and retrieved.
 */
public interface DefinitionPersister<T extends Definition> {
	
	/**
	 * @return the definition with the given id among those managed by this persister
	 */
	public T getDefinition(Integer id);
	
	/**
	 * @return the definition with the given uuid among those managed by this persister
	 */
	public T getDefinitionByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired Definitions in the returned list
	 * @return All definitions whose persistence is managed by this persister
	 */
	public List<T> getAllDefinitions(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Querys in the count
	 * @return the number of saved Definitions
	 */
	public int getNumberOfDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link Definition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<Definition> objects whose name contains the passed name
	 */
	public List<T> getDefinitions(String name, boolean exactMatchOnly) throws APIException;
	
	/**
	 * Persists a Definition, either as a save or update.
	 * @return the Definition that was passed in
	 */
	public T saveDefinition(T definition);
	
	/**
	 * Deletes a definition from the database.
	 */
	public void purgeDefinition(T definition);
}
