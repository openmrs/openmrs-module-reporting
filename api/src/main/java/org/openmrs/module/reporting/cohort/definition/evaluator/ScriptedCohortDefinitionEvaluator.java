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
