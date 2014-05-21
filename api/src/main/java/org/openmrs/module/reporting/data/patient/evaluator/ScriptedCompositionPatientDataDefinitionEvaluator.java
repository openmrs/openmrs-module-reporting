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
package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ScriptedCompositionPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Evaluates a ScriptedCompositionPatientDataDefinition to produce a PatientData
 */
@Handler(supports = ScriptedCompositionPatientDataDefinition.class, order = 50)
public class ScriptedCompositionPatientDataDefinitionEvaluator implements PatientDataEvaluator {
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		ScriptedCompositionPatientDataDefinition pd = (ScriptedCompositionPatientDataDefinition) definition;
		Map<String, Mapped<PatientDataDefinition>> containedDataDefintions = pd.getContainedDataDefinitions();
		Map<String, EvaluatedPatientData> evaluatedContainedDataDefinitions = new HashMap<String, EvaluatedPatientData>();
		
		// fail if passed-in definition has no patient data definitions on it
		if (containedDataDefintions.size() < 1) {
			throw new EvaluationException("No patient data definition(s) found on this ScriptedCompositionPatientDataDefinition");
		}
		
		// fail if passed-in definition has no script code specified
		if (pd.getScriptCode() == null) {
			throw new EvaluationException("No script code found on this ScriptedCompositionPatientDataDefinition");
		}
		
		// fail if passed-in definition has no script type specified
		if (pd.getScriptType() == null) {
			throw new EvaluationException("No script type found on this ScriptedCompositionPatientDataDefinition");
		}
		
		//evaluate the contained data definitions and put the results in the "evaluatedContainedDataDefinitions" map
		for (Entry<String, Mapped<PatientDataDefinition>> d : containedDataDefintions.entrySet()) {
			EvaluatedPatientData patientDataResult = Context.getService(PatientDataService.class).evaluate(d.getValue(), context);
			evaluatedContainedDataDefinitions.put(d.getKey(), patientDataResult);
		}
		
		EvaluatedPatientData evaluationResult = new EvaluatedPatientData(pd, context);
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine scriptEngine = manager.getEngineByName(pd.getScriptType().getLanguage());
		scriptEngine.put("evaluationContext", context);
		scriptEngine.put("parameters", context.getParameterValues());

		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort == null) {
            baseCohort = Cohorts.allPatients(context);
		}

		for (Integer pId : baseCohort.getMemberIds()) { //iterate across all patients
		
			for (Entry<String, EvaluatedPatientData> dataEntry : evaluatedContainedDataDefinitions.entrySet()) {
				Object o = dataEntry.getValue().getData().get(pId);
				scriptEngine.put(dataEntry.getKey(), o); //put the definition result key and the corresponding actual object directly in the scripting context
			}
			
			try {
				Object o = scriptEngine.eval(pd.getScriptCode()); //execute the script for the current patient.
				evaluationResult.addData(pId, o); //put the returned object value in the evaluationResult for the current patient
			}
			catch (ScriptException ex) {
				throw new EvaluationException("An error occured while evaluating script", ex);
			}
			
		}
		
		return evaluationResult;
		
	}
}
