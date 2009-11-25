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
package org.openmrs.module.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.DrugRegimenActiveCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsActiveCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsStartedCohortDefinition;
import org.openmrs.module.cohort.query.service.CohortQueryService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.regimen.CohortRegimenHistory;

/**
 * 
 */
@Handler(supports={DrugRegimenActiveCohortDefinition.class})
public class DrugRegimenActiveCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private Log log = LogFactory.getLog(getClass());
	
	/**
	 * Default Constructor
	 */
	public DrugRegimenActiveCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	DrugRegimenActiveCohortDefinition definition = (DrugRegimenActiveCohortDefinition) cohortDefinition;

    	CohortRegimenHistory regimenHistory = new CohortRegimenHistory(context.getBaseCohort());
   
    	// Search for patients that are taking a first line regimen as of given date
    	if (definition.getOnFirstLineRegimen()) 
    		return regimenHistory.getPatientsOnFirstLineRegimenAsOfDate(definition.getAsOfDate());

    	// Search for patients that are taking a second line regimen as of given date.
    	if (definition.getOnSecondLineRegimen())
    		return regimenHistory.getPatientsOnSecondLineRegimenAsOfDate(definition.getAsOfDate());

    	// Otherwise return patients on any regimen as of date 
    	return regimenHistory.getPatientsOnRegimenAsOfDate(definition.getAsOfDate());
    }
}