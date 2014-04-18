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
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an PatientCharacteristicCohortDefinition and produces a Cohort
 */
@Handler(supports={AgeCohortDefinition.class})
public class AgeCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public AgeCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return only patients born on or before the evaluation date
     * @should return only non voided patients
     * @should return only patients in the given age range
     * @should only return patients with unknown age if specified
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	AgeCohortDefinition acd = (AgeCohortDefinition) cohortDefinition;
    	CohortQueryService cqs = Context.getService(CohortQueryService.class);
    	
    	Cohort c = cqs.getPatientsWithAgeRange(acd.getMinAge(), acd.getMinAgeUnit(), acd.getMaxAge(), acd.getMaxAgeUnit(), 
    									   acd.isUnknownAgeIncluded(), acd.getEffectiveDate());
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }
}