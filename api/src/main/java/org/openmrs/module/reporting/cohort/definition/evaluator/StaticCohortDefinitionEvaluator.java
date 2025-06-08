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
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an StaticCohortDefinition and produces a Cohort
 */
@Handler(supports={StaticCohortDefinition.class})
public class StaticCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public StaticCohortDefinitionEvaluator() {}

	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	Cohort c = ((StaticCohortDefinition) cohortDefinition).getCohort();
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }
}