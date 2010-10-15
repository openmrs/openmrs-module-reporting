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
package org.openmrs.module.reporting.definition.service;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.reporting.evaluation.Definition;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate Definitions
 */
@Transactional
public interface SerializedDefinitionService extends OpenmrsService {
	
	/**
	 * @return all supported Definition Types
	 */
	public <T extends Definition> List<Class<T>> getSupportedDefinitionTypes();
	
	/**
	 * @param id
	 * @return the Definition with the given id
	 */
	public <T extends Definition> T getDefinition(Class<T> definitionType, Integer id);
	
	/**
	 * @param uuid
	 * @return the Definition with the given uuid
	 */
	public <T extends Definition> T getDefinitionByUuid(Class<T> definitionType, String uuid);
	
	/**
	 * @param includeRetired - if true, include retired Definitions in the returned list
	 * @return all definitions
	 */
	public <T extends Definition> List<T> getAllDefinitions(Class<T> definitionType, boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Definitions in the count
	 * @return the number of saved Cohort Definitions
	 */
	public <T extends Definition> int getNumberOfDefinitions(Class<T> definitionType, boolean includeRetired);
	
	/**
	 * Returns a List of {@link Definition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<Definition> objects whose name contains the passed name
	 */
	public <T extends Definition> List<T> getDefinitions(Class<T> definitionType, String name, boolean exactMatchOnly) throws APIException;
	
	/**
	 * Persists a Definition, either as a save or update.
	 * @param definition
	 * @return the Definition that was passed in
	 */
	public <T extends Definition> T saveDefinition(T definition);
	
	/**
	 * Deletes a Definition from the database.
	 * @param definition
	 */
	public <T extends Definition> void purgeDefinition(T definition);
	
	/**
	 * Deletes a Definition from the database with the given uuid
	 * @param id
	 */
	public void purgeDefinition(String uuid);
	
	/**
	 * @param definitionType the type of Definition
	 * @return the SerializedObject with this uuid
	 */
    public SerializedObject getSerializedDefinitionByUuid(String uuid);
    
    /**
     * Returns all invalid SerializedObject Definitions, regardless of type
     * @param includeRetired indicates whether to also include retired Definitions in the count
     * @return the SerializedObjects that cannot be deserialized
     */
    public List<SerializedObject> getInvalidDefinitions(boolean includeRetired);
	
	/**
	 * @param definitionType the type of Definition
	 * @param includeRetired indicates whether to also include retired Definitions in the count
	 * @return the SerializedObjects that cannot be deserialized
	 */
    public <T extends Definition> List<SerializedObject> getInvalidDefinitions(Class<T> definitionType, boolean includeRetired);
    
	/**
	 * @param serializedDefinition the SerializedObject definition to save
	 * @return the SerializedObject
	 */
    public void saveSerializedDefinition(SerializedObject serializedDefinition);
}
