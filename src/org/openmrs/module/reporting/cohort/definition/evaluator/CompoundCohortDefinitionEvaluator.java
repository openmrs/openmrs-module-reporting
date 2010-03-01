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
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Evaluates an InverseCohortDefinition and produces a Cohort
 */
@Handler(supports={CompoundCohortDefinition.class})
public class CompoundCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public CompoundCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	CompoundCohortDefinition ccd = (CompoundCohortDefinition) cohortDefinition;
		Cohort runningCohort = null;
		for (Mapped<CohortDefinition> d : ccd.getDefinitions()) {
			EvaluationContext childContext = EvaluationContext.cloneForChild(context, d);
			Cohort c = Context.getService(CohortDefinitionService.class).evaluate(d.getParameterizable(), childContext);
			if (runningCohort == null) {
				runningCohort = c;
			}
			else {
				if (ccd.getOperator() == BooleanOperator.AND) {
					runningCohort = Cohort.intersect(runningCohort, c);
				}
				else {
					runningCohort = Cohort.union(runningCohort, c);
				}
			}
		}
		return runningCohort;
    }
}