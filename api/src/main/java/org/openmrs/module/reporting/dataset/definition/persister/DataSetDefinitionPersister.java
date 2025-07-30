/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.persister;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.persister.DefinitionPersister;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of DataSetDefinition implementations
 */
public interface DataSetDefinitionPersister extends DefinitionPersister<DataSetDefinition> {
	
	/**
	 * Gets the {@link DataSetDefinition} that matches the given id
	 * 
	 * @param id the id to match
	 * @return the {@link DataSetDefinition} with the given id among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return DataSetDefinition when exists
	 */
	public DataSetDefinition getDefinition(Integer id);
	
	/**
	 * Gets the {@link DataSetDefinition} that matches the given uuid
	 * 
	 * @param uuid	the uuid to match
	 * @return the {@link DataSetDefinition} with the given uuid among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return {@link DataSetDefinition} when exists
	 */
	public DataSetDefinition getDefinitionByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired {@link DataSetDefinition} in the returned List
	 * @return All {@link DataSetDefinition} whose persistence is managed by this persister
	 * 
	 * @should get all {@link DataSetDefinition} including retired
	 * @should get all {@link DataSetDefinition} not including retired
	 */
	public List<DataSetDefinition> getAllDefinitions(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired DataSetDefinitions in the count
	 * @return the number of saved DataSetDefinitions
	 */
	public int getNumberOfDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link DataSetDefinition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * 
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * 
	 * @throws APIException
	 * @return a List<DataSetDefinition> objects whose name contains the passed name
	 */
	public List<DataSetDefinition> getDefinitions(String name, boolean exactMatchOnly);
	
	/**
	 * Saves the given {@link DataSetDefinition} to the system.
	 * 
	 * @param datasetDefinition	the {@link DataSetDefinition} to save
	 * @return the {@link DataSetDefinition} that was 
	 * 
	 * @should create new {@link DataSetDefinition}
	 * @should update existing {@link DataSetDefinition}
	 * @should set identifier after save
	 */
	public DataSetDefinition saveDefinition(DataSetDefinition datasetDefinition);
	
	/**
	 * Deletes a {@link DataSetDefinition} from the system.
	 * 
	 * @param datasetDefinition	the {@link DataSetDefinition} to purge
	 * 
	 * @should remove the DataSetDefinition
	 */
	public void purgeDefinition(DataSetDefinition dataSetDefinition);
}
