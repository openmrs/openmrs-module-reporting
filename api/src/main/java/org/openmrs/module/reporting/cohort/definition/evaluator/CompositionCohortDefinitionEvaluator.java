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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.util.CohortExpressionParser;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

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
	 * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
	 * @should evaluate a definition with a search that contains an under score or space
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
    	CompositionCohortDefinition composition = (CompositionCohortDefinition) cohortDefinition;
    	try {
        	CompositionCohortDefinition clone = (CompositionCohortDefinition) BeanUtils.cloneBean(composition);
        	String expression = clone.getCompositionString();
        	Set<String> searches = new HashSet<String>();
        	for (String s : clone.getSearches().keySet()) {
        		searches.add(s);
            }

        	String replacement = "XXXXXX";
        	for (String search : searches) {
        		expression = StringUtils.replace(expression, search, replacement+="X");
        		//change the keys to the new ones
        		Mapped<CohortDefinition> def = clone.getSearches().get(search);
        		clone.getSearches().put(replacement, def);
        		clone.getSearches().remove(search);
            }
    		clone.setCompositionString(expression);
    		Cohort c = CohortExpressionParser.evaluate(clone, context);
    		return new EvaluatedCohort(c, cohortDefinition, context);
    	} catch (MissingDependencyException ex) {
    		String name = composition.getName() != null ? composition.getName() : composition.getCompositionString();
    		throw new EvaluationException("sub-query '" + ex.getPropertyThatFailed() + "' of composition '" + name + "'", ex);
    	}
    	catch (Exception ex) {
    		throw new EvaluationException("Error:", ex);
    	}
    }
}