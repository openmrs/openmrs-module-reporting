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
import java.util.Map;
import java.util.Set;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.cohort.definition.DefinitionTag;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate Definitions
 */
@Transactional
public interface DefinitionService<T extends Definition> extends OpenmrsService {
	
	/**
	 * @return the Definition class that this service manages
	 */
	public Class<T> getDefinitionType();
	
	/**
	 * @return a List of all Definition classes that are available for evaluation
	 */
	public List<Class<? extends T>> getDefinitionTypes();
	
	/**
	 * @param type the Class<Definition> to retrieve
	 * @param id the id to retrieve for the given type
	 * @return the Definition that matches the given type and id
	 */
	@Transactional(readOnly = true)
	public <D extends T> D getDefinition(Class<D> type, Integer id) throws APIException;
	
	/**
	 * @param uuid
	 * @return the Definition with the given uuid
	 */
	@Transactional(readOnly = true)
	public T getDefinitionByUuid(String uuid) throws APIException;
	
	/**
	 * Helper method which checks that either uuid or type is passed, and returns either the saved
	 * Definition with the passed uuid, or a new instance of the Definition represented by the
	 * passed type. Throws an IllegalArgumentException if any of this is invalid.
	 * 
	 * @param uuid
	 * @param type
	 * @return the Definition with the given uuid and type
	 */
	@Transactional(readOnly = true)
	public T getDefinition(String uuid, Class<? extends T> type);
	
	/**
	 * @param includeRetired - if true, include retired {@link Definition} in the returned list
	 * @return All {@link Definition} whose persistence is managed by this persister
	 */
	@Transactional(readOnly = true)
	public List<T> getAllDefinitions(boolean includeRetired);
	
	/**
	 * @param includeRetired
	 * @return lightweight summaries for all definitions managed by this service
	 */
	@Transactional(readOnly = true)
	public List<DefinitionSummary> getAllDefinitionSummaries(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Definitions in the count
	 * @return the number of saved Cohort Definitions
	 */
	@Transactional(readOnly = true)
	public int getNumberOfDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link Definition} whose name contains the passed name. An empty list will
	 * be returned if there are none found. Search is case insensitive.
	 * 
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * @throws APIException
	 * @return a List<Definition> objects whose name contains the passed name
	 */
	@Transactional(readOnly = true)
	public List<T> getDefinitions(String name, boolean exactMatchOnly);
	
	/**
	 * Persists a Definition, either as a save or update.
	 * 
	 * @param definition
	 * @return the Definition that was passed in
	 */
	@Transactional
	public <D extends T> D saveDefinition(D definition) throws APIException;
	
	/**
	 * Deletes a Definition from the database.
	 * 
	 * @param definition the Definition to purge
	 */
	@Transactional
	public void purgeDefinition(T definition);
	
	/**
	 * Evaluates the passed Mapped Definition for the given EvaluationContext
	 * 
	 * @param definition Mapped<Definition> to evaluate
	 * @param context context to use during evaluation
	 * @return the evaluated definition
	 */
	@Transactional(readOnly = true)
	public Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * Evaluates the passed Definition for the given EvaluationContext<br/>
	 * 
	 * @param definition Definition to evaluate
	 * @param context context to use during evaluation
	 * @return the evaluated definition
	 */
	@Transactional(readOnly = true)
	public Evaluated<T> evaluate(T definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * Gets all tags that have been applied to any of this service's definition type's subclasses
	 * 
	 * @return a list of tags
	 * @should get all tags applied to any definition of the service type
	 */
	@Transactional(readOnly = true)
	public Set<String> getAllTags();
	
	/**
	 * Gets definition summaries for all definitions of this service's definition type's subclasses
	 * that have the given tag
	 * 
	 * @param tag the tag to match against
	 * @return a list of definition summaries
	 * @should get all definition as summaries with the specified tag
	 */
	@Transactional(readOnly = true)
	public List<DefinitionSummary> getAllDefinitionsHavingTag(String tag);
	
	/**
	 * Gets all DefinitionTags for all definition of this service's definition type's subclasses
	 * (e.g. all tags on CohortDefinitions for CohortDefinitionService)
	 * 
	 * @return a list of definition tags
	 * @should get all definition tags applied to any definition of the service type
	 */
	@Transactional(readOnly = true)
	public List<DefinitionTag> getAllDefinitionTags();
	
	/**
	 * Fetches all definitions of this service's definition type's subclasses grouped by the tags
	 * they have (note that definitions may have multiple tags, so a definition can appear in
	 * multiple lists)
	 * 
	 * @return map of tags and definition summaries
	 * @should get all definitions as summaries of the service type grouped by tags
	 */
	@Transactional(readOnly = true)
	public Map<String, List<DefinitionSummary>> getAllDefinitionsByTag();
	
	/**
	 * Adds the specified tag to the specified defintionm, silently does not add duplicates
	 * 
	 * @param definition the definition to add the tag
	 * @param tag the tag to add to the definition
	 * @return true if the tag was added otherwise false
	 * @should add the specified tag to the specified definition
	 */
	public boolean addTagToDefinition(T definition, String tag);
	
	/**
	 * Silently returns if the definition doesn't have the given tag
	 * 
	 * @param definition the definition from which to remove the tag
	 * @param tag the tag to remove from the definition
	 * @should remove the specified tag from the specified definition
	 */
	public void removeTagFromDefinition(T definition, String tag);
	
	/**
	 * Checks if the specified definition has the specified tag
	 * 
	 * @param definition the definition to check
	 * @param tag the name of the tag to check for
	 * @return true if the definition has the tag otherwise false
	 * @should return true if the definition has the tag
	 * @should return false if the definition doesnt have the tag
	 * @should return false if the definition is null
	 * @should return false if the tag is null
	 * @should return false if the definition has a null uuid
	 */
	public boolean hasTag(T definition, String tag);
	
	/**
	 * Checks if the definition with the specified uuid has the specified tag
	 * 
	 * @param definitionUuid the uuid of the definition to check
	 * @param tag the name of the tag to check for
	 * @return true if the matching definition has the tag otherwise false
	 * @should return true if the matching definition has the tag
	 * @should return false if the matching definition doesnt have the tag
	 * @should return false if the uuid is null
	 * @should return false if the tag parameter is null
	 */
	public boolean hasTag(String definitionUuid, String tag);
	
	/**
	 * Gets all the tags applied to the specified definition
	 * 
	 * @param Definition the definition to match against
	 * @return a list of tags for the definition
	 * @should get all the tags applied to the definition
	 */
	public List<String> getTags(T definition);
}
