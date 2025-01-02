/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.service;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate Definitions
 */
@Transactional
public interface SerializedDefinitionService extends OpenmrsService {
	
	/**
	 * @return all supported Definition Types
	 */
    @Authorized
	public <T extends Definition> List<Class<T>> getSupportedDefinitionTypes();
	
	/**
	 * @param id
	 * @return the Definition with the given id
	 */
    @Authorized
	public <T extends Definition> T getDefinition(Class<T> definitionType, Integer id);
	
	/**
	 * @param uuid
	 * @return the Definition with the given uuid
	 */
    @Authorized
	public <T extends Definition> T getDefinitionByUuid(Class<T> definitionType, String uuid);
	
	/**
	 * @param includeRetired - if true, include retired Definitions in the returned list
	 * @return all definitions
	 */
    @Authorized
	public <T extends Definition> List<T> getAllDefinitions(Class<T> definitionType, boolean includeRetired);

	/**
	 * @param definitionType
	 * @param includeRetired
	 * @return lightweight summaries of all definitions of the specified class
	 */
    @Authorized
	public <T extends Definition> List<DefinitionSummary> getAllDefinitionSummaries(Class<T> definitionType, boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Definitions in the count
	 * @return the number of saved Cohort Definitions
	 */
    @Authorized
	public <T extends Definition> int getNumberOfDefinitions(Class<T> definitionType, boolean includeRetired);
	
	/**
	 * Returns a List of {@link Definition} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<Definition> objects whose name contains the passed name
	 */
    @Authorized
	public <T extends Definition> List<T> getDefinitions(Class<T> definitionType, String name, boolean exactMatchOnly) throws APIException;
	
	/**
	 * Persists a Definition, either as a save or update.
	 * @param definition
	 * @return the Definition that was passed in
	 */
    @Authorized
	public <T extends Definition> T saveDefinition(T definition);
	
	/**
	 * Deletes a Definition from the database.
	 * @param definition
	 */
    @Authorized
	public <T extends Definition> void purgeDefinition(T definition);
	
	/**
	 * Deletes a Definition from the database with the given uuid
	 * @param uuid
	 */
    @Authorized
	public void purgeDefinition(String uuid);
	
	/**
	 * @param uuid the uuid of Definition
	 * @return the SerializedObject with this uuid
	 */
    @Authorized
    public SerializedObject getSerializedDefinitionByUuid(String uuid);
    
    /**
     * Returns all invalid SerializedObject Definitions, regardless of type
     * @param includeRetired indicates whether to also include retired Definitions in the count
     * @return the SerializedObjects that cannot be deserialized
     */
    @Authorized
    public List<SerializedObject> getInvalidDefinitions(boolean includeRetired);
	
	/**
	 * @param definitionType the type of Definition
	 * @param includeRetired indicates whether to also include retired Definitions in the count
	 * @return the SerializedObjects that cannot be deserialized
	 */
    @Authorized
    public <T extends Definition> List<SerializedObject> getInvalidDefinitions(Class<T> definitionType, boolean includeRetired);
    
	/**
	 * @param serializedDefinition the SerializedObject definition to save
	 * @return the SerializedObject
	 */
    @Authorized
    public void saveSerializedDefinition(SerializedObject serializedDefinition);

}
