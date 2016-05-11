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
import org.openmrs.module.reporting.cohort.definition.TextObsCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a TextObsCohortDefinition and produces a Cohort
 */
@Handler(supports={TextObsCohortDefinition.class})
public class TextObsCohortDefinitionEvaluator extends BaseObsCohortDefinitionEvaluator {
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 * 
	 * @should test any with many properties specified
	 * @should test last with many properties specified
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		TextObsCohortDefinition cd = (TextObsCohortDefinition) cohortDefinition;
		Cohort c = getPatientsHavingObs(cd, null, null, null, null, cd.getOperator(), cd.getValueList(), context);
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
