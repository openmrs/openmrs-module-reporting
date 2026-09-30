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
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports={BirthAndDeathCohortDefinition.class})
public class BirthAndDeathCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    EvaluationService evaluationService;
	
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
		BirthAndDeathCohortDefinition cd = (BirthAndDeathCohortDefinition) cohortDefinition;

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("p.patient.id");
        q.from(Patient.class, "p");
        q.whereGreaterOrEqualTo("p.birthdate", cd.getBornOnOrAfter());
        q.whereLessOrEqualTo("p.birthdate", cd.getBornOnOrBefore());
        q.whereEqual("p.dead", cd.getDied());
        q.whereGreaterOrEqualTo("p.deathDate", cd.getDiedOnOrAfter());
        q.whereLessOrEqualTo("p.deathDate", cd.getDiedOnOrBefore());
        Cohort c = new Cohort(evaluationService.evaluateToList(q, Integer.class, context));

		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
