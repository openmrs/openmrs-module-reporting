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
package org.openmrs.module.dataset.definition.persister;

import java.util.List;
import java.util.UUID;

import org.openmrs.module.dataset.definition.DataSetDefinition;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of CohortDefinition implementations
 */
public interface DataSetDefinitionPersister {
		
	/**
	 * Returns true if this persister can handle the given class, false 
	 * if the persister cannot handle it.
	 * 
	 * TODO Should be part of the "handler" class.
	 * 
	 * @param clazz		the class to be handled
	 * @return	true if the class can be handled, false otherwise
	 * 
	 * @should handle registered class
	 * @should not handle unregistered class
	 */
	public Boolean canPersist(Class<? extends DataSetDefinition> clazz);
	
	/**
	 * @return All dataset definitions whose persistence is managed by this persister
	 * 
	 * @should get all dataset definitions not including retired
	 */
	public List<DataSetDefinition> getAllDataSetDefinitions();

	/**
	 * @param includeRetired - if true, include retired CohortDefinitions in the returned list
	 * @return All dataset definitions whose persistence is managed by this persister
	 * 
	 * @should get all dataset definitions including retired
	 * @should get all dataset definitions not including retired
	 */
	public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired);
	
	/**
	 * Gets the dataset definition that matches the given identifier.
	 * 
	 * @param id	the identifier to match
	 * @return the dataset definition with the given id among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return dataset definition when exists
	 */
	public DataSetDefinition getDataSetDefinition(Integer id);
	
	/**
	 * Gets the dataset definition that matches the given unique identifier.
	 * 
	 * @param uuid	the identifier to match
	 * @return the dataset definition with the given id among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return dataset definition when exists
	 */
	public DataSetDefinition getDataSetDefinition(UUID uuid);
		
	
	/**
	 * Gets the dataset definition that matches the given name.
	 * 
	 * @param name	the name to match
	 * @return the dataset definition with the given name among those managed by this persister
	 * 
	 * @should return null when name does not match
	 * @should return dataset definition when name matches
	 * @should return first dataset definition when name matches multiple
	 */
	public DataSetDefinition getDataSetDefinitionByName(String name);
	
	/**
	 * Saves the given dataset definition to the system.
	 * 
	 * @param datasetDefinition	the dataset definition to save
	 * @return the dataset definition that was 
	 * 
	 * @should create new dataset definition
	 * @should update existing dataset definition
	 * @should set identifier after save
	 */
	public DataSetDefinition saveDataSetDefinition(DataSetDefinition datasetDefinition);
	
	/**
	 * Deletes a dataset definition from the system.
	 * 
	 * @param datasetDefinition	the dataset definition to purge
	 * 
	 * @should remove dataset definition
	 */
	public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition);
}
