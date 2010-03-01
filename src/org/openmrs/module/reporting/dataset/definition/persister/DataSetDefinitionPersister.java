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
package org.openmrs.module.reporting.dataset.definition.persister;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of DataSetDefinition implementations
 */
public interface DataSetDefinitionPersister {
	
	/**
	 * Gets the {@link DataSetDefinition} that matches the given id
	 * 
	 * @param id the id to match
	 * @return the {@link DataSetDefinition} with the given id among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return DataSetDefinition when exists
	 */
	public DataSetDefinition getDataSetDefinition(Integer id);
	
	/**
	 * Gets the {@link DataSetDefinition} that matches the given uuid
	 * 
	 * @param uuid	the uuid to match
	 * @return the {@link DataSetDefinition} with the given uuid among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return {@link DataSetDefinition} when exists
	 */
	public DataSetDefinition getDataSetDefinitionByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired {@link DataSetDefinition} in the returned List
	 * @return All {@link DataSetDefinition} whose persistence is managed by this persister
	 * 
	 * @should get all {@link DataSetDefinition} including retired
	 * @should get all {@link DataSetDefinition} not including retired
	 */
	public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired);
	
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
	public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly);
	
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
	public DataSetDefinition saveDataSetDefinition(DataSetDefinition datasetDefinition);
	
	/**
	 * Deletes a {@link DataSetDefinition} from the system.
	 * 
	 * @param datasetDefinition	the {@link DataSetDefinition} to purge
	 * 
	 * @should remove the DataSetDefinition
	 */
	public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition);
}
