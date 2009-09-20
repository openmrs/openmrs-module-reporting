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
package org.openmrs.module.cohort.definition.evaluator;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.cohort.definition.util.CohortExpressionParser;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * Evaluates an CompositionCohortDefinition and produces a Cohort
 */
@Handler(supports={CompositionCohortDefinition.class})
public class CompositionCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public CompositionCohortDefinitionEvaluator() {}

	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	CompositionCohortDefinition d = (CompositionCohortDefinition) cohortDefinition;
    	
		List<Object> tokens = CohortExpressionParser.parseIntoTokens(d.getCompositionString());
		return CohortExpressionParser.evaluate(tokens, d, context);
    }
}