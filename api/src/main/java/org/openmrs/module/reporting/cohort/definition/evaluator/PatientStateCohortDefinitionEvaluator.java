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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an PatientStateCohortDefinition and produces a Cohort
 */
@Handler(supports={PatientStateCohortDefinition.class})
public class PatientStateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public PatientStateCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return patients in the specified states before the start date
     * @should return patients in the specified states after the start date
	 * @should return patients in the specified states before the end date
	 * @should return patients in the specified states after the end date
	 * @should find patients in specified states on the before start date if passed in time is at midnight
	 * @should find patients in specified states on the before end date if passed in time is at midnight
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	PatientStateCohortDefinition d = (PatientStateCohortDefinition) cohortDefinition;
    	Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingStates(d.getStates(),
    		d.getStartedOnOrAfter(),
    		d.getStartedOnOrBefore(),
    		d.getEndedOnOrAfter(),
    		d.getEndedOnOrBefore());
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }
}