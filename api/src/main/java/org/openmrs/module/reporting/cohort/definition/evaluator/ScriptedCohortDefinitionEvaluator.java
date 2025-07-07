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

import java.util.Collection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ScriptedCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an StaticCohortDefinition and produces a Cohort
 */
@Handler(supports = { ScriptedCohortDefinition.class })
public class ScriptedCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public ScriptedCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		ScriptedCohortDefinition scriptedCohortDefinition = (ScriptedCohortDefinition) cohortDefinition;
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine scriptEngine = manager.getEngineByName(scriptedCohortDefinition.getScriptType().getLanguage());
		scriptEngine.put("context", context);
		scriptEngine.put("parameters", context.getParameterValues());
		try {
			Object result = scriptEngine.eval(scriptedCohortDefinition.getScriptCode());
			
			Cohort cohort = null;
			if (result instanceof Cohort) {
				cohort = (Cohort) result;
			} else {
				cohort = new Cohort((Collection<Integer>) result);
			}
			
			return new EvaluatedCohort(cohort, cohortDefinition, context);
		}
		catch (ScriptException ex) {
			throw new EvaluationException("An error occured while evaluating script", ex);
		}
		catch (ClassCastException ex) {
			throw new EvaluationException(
			        "A Scripted Cohort Definition must return either a Cohort or a Collection<Integer>", ex);
		}
	}
}
