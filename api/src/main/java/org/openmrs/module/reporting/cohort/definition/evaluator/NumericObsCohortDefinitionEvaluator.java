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
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a NumericObsCohortDefinition and produces a Cohort
 */
@Handler(supports={NumericObsCohortDefinition.class})
public class NumericObsCohortDefinitionEvaluator extends BaseObsCohortDefinitionEvaluator {
	
	public NumericObsCohortDefinitionEvaluator() { } 
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 * 
	 * @should get patients with any obs of a specified concept
	 * @should test any with many properties specified
	 * @should test avg with many properties specified
	 * @should test last with many properties specified 
	 * @should should find patients with obs within the specified time frame
	 */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
        NumericObsCohortDefinition cd = (NumericObsCohortDefinition) cohortDefinition;
        Cohort c = getPatientsHavingObs(cd, cd.getOperator1(), cd.getValue1(), cd.getOperator2(), cd.getValue2(), null, null, context);
        return new EvaluatedCohort(c, cohortDefinition, context);
    }
	
}
