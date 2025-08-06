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
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a DateObsCohortDefinition and produces a Cohort
 */
@Handler(supports={DateObsCohortDefinition.class})
public class DateObsCohortDefinitionEvaluator extends BaseObsCohortDefinitionEvaluator {
	
	public DateObsCohortDefinitionEvaluator() { }
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 * 
	 * @should test any with many properties specified
	 * @should find nobody if no patients match
	 */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
        DateObsCohortDefinition cd = (DateObsCohortDefinition) cohortDefinition;
        Cohort c = getPatientsHavingObs(cd, cd.getOperator1(), cd.getValue1(), cd.getOperator2(), cd.getValue2(), null, null, context);
        return new EvaluatedCohort(c, cohortDefinition, context);
    }
	
}
