/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
