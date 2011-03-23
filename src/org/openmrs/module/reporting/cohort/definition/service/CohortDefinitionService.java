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
package org.openmrs.module.reporting.cohort.definition.service;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.history.CohortDefinitionSearchHistory;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate CohortDefinitions
 */
@Transactional
public interface CohortDefinitionService extends DefinitionService<CohortDefinition> {
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * @see DefinitionService#evaluate(Mapped<Definition>, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext context) throws EvaluationException;
	
	//******* TODO: DO WE REMOVE EVERYTHING BELOW HERE? (MS 3/16/10) ******
	
	/**
	 * Gets the current user's CohortDefinitionSearchHistory, or null if none exists yet  
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public CohortDefinitionSearchHistory getCurrentUsersCohortDefinitionSearchHistory() throws APIException;
	
	/**
	 * Sets the current user's CohortDefinitionSearchHistory 
	 * @param history
	 * @throws APIException
	 */
	@Transactional
	public void setCurrentUsersCohortDefinitionSearchHistory(CohortDefinitionSearchHistory history) throws APIException;
	
	/**
	 * Removes the current user's search history from persisted storage
	 * 
	 * @throws APIException
	 */
	public void clearCurrentUsersCohortDefinitionSearchHistory() throws APIException;
	
}
