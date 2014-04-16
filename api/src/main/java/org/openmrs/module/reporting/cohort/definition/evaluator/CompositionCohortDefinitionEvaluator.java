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
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.util.CohortExpressionParser;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;

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
     * @throws EvaluationException 
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
    	CompositionCohortDefinition composition = (CompositionCohortDefinition) cohortDefinition;
    	try {
    		Cohort c = CohortExpressionParser.evaluate(composition, context);
    		return new EvaluatedCohort(c, cohortDefinition, context);
    	} catch (MissingDependencyException ex) {
    		String name = composition.getName() != null ? composition.getName() : composition.getCompositionString();
    		throw new EvaluationException("sub-query '" + ex.getPropertyThatFailed() + "' of composition '" + name + "'", ex);
    	}
    }
}