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
