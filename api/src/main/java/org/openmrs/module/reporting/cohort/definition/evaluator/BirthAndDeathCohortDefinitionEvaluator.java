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
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports={BirthAndDeathCohortDefinition.class})
public class BirthAndDeathCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
    public BirthAndDeathCohortDefinitionEvaluator() { }
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * 
	 * @should find patients by birth range
	 * @should find patients by death range
	 * @should find patients by birth range and death range
	 * @should find patients born on the onOrBefore date if passed in time is at midnight
	 * @should find patients that died on the onOrBefore date if passed in time is at midnight
	 * @should find patients born after the specified date
	 * @should find patients that died after the specified date
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		BirthAndDeathCohortDefinition definition = (BirthAndDeathCohortDefinition) cohortDefinition;
		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingBirthAndDeath(
			definition.getBornOnOrAfter(),
			definition.getBornOnOrBefore(),
			definition.getDiedOnOrAfter(),
			definition.getDiedOnOrBefore());
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
