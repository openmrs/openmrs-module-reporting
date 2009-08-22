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
package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.EncounterDataSet;
import org.openmrs.module.dataset.ProgramDataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.ProgramDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link ProgramDataSetDefinition} 
 * and produces an {@link ProgramDataSet}
 * 
 * @see EncounterDataSetDefinition
 * @see EncounterDataSet
 */
@Handler(supports={ProgramDataSetDefinition.class})
public class ProgramDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public ProgramDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		ProgramDataSetDefinition definition = (ProgramDataSetDefinition) dataSetDefinition;
		Cohort patients = context.getBaseCohort();
		if (definition.getCohortDefinition() != null) {
			Cohort c = Context.getService(CohortDefinitionService.class).evaluate(definition.getCohortDefinition(), context);
			patients = (patients == null ? c : Cohort.intersect(patients, c));
		}
		
		ProgramDataSet dataSet = new ProgramDataSet();
		dataSet.setDataSetDefinition(definition);
		dataSet.setEvaluationContext(context);
		List<Program> programs = new ArrayList<Program>(definition.getPrograms());
		List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patients, programs);
		dataSet.setData(patientPrograms);
		return dataSet;
	}
}
