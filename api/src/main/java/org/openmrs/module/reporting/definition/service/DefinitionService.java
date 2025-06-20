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

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface for methods used to manage and evaluate Definitions
 */
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
	 * @should deserialize CohortIndicatorAndDimensionDataSetDefinition
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
	 * @param tagName the tag name to look up
	 * @return all Definitions that are tagged with the given tagName
	 */
	@Transactional(readOnly = true)
	public List<T> getDefinitionsByTag(String tagName);
	
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
	public Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * Evaluates the passed Definition for the given EvaluationContext<br/>
	 * 
	 * @param definition Definition to evaluate
	 * @param context context to use during evaluation
	 * @return the evaluated definition
	 */
	public Evaluated<T> evaluate(T definition, EvaluationContext context) throws EvaluationException;
}
