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

import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates an ProgramStatesForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=ProgramStatesForPatientDataDefinition.class, order=50)
public class ProgramStatesForPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return the patient state that is active on a given date
	 * @should return patient states started on or after a given date
	 * @should return patient states started on or before a given date
	 * @should return patient states ended on or after a given date
	 * @should return patient states ended on or before a given date
	 * @should return the first patient state by start date
	 * @should return the last patient state by start date
	 * @should return a list of patient states for each patient 
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		ProgramStatesForPatientDataDefinition def = (ProgramStatesForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("ps.patientProgram.patient.patientId", "ps");
		q.from(PatientState.class, "ps");
		q.wherePatientIn("ps.patientProgram.patient.patientId", context);
		q.whereEqual("ps.state.programWorkflow", def.getWorkflow());
		q.whereEqual("ps.state", def.getState());
		q.whereEqual("ps.patientProgram.location", def.getLocation());
		q.whereGreaterOrEqualTo("ps.startDate", def.getStartedOnOrAfter());
		q.whereLessOrEqualTo("ps.startDate", def.getStartedOnOrBefore());
		q.whereGreaterOrEqualTo("ps.endDate", def.getEndedOnOrAfter());
		q.whereLessOrEqualTo("ps.endDate", def.getEndedOnOrBefore());

		if (def.getActiveOnDate() != null) {
			q.whereLessOrEqualTo("ps.startDate", def.getActiveOnDate());
			q.whereGreaterOrNull("ps.endDate", def.getActiveOnDate());
		}

		if (def.getWhich() == TimeQualifier.LAST) {
			q.orderDesc("ps.startDate").orderDesc("ps.patientProgram.dateEnrolled");
		}
		else {
			q.orderAsc("ps.startDate").orderAsc("ps.patientProgram.dateEnrolled");
		}

		List<Object[]> queryResult = evaluationService.evaluateToList(q);

		ListMap<Integer, PatientState> statesForPatients = new ListMap<Integer, PatientState>();
		for (Object[] row : queryResult) {
			statesForPatients.putInList((Integer)row[0], (PatientState)row[1]);
		}
		
		for (Integer pId : statesForPatients.keySet()) {
			List<PatientState> l = statesForPatients.get(pId);
			if (def.getDataType() == PatientState.class) {
				c.addData(pId, l.get(0));
			}
			else {
				c.addData(pId, l);
			}
		}
		
		return c;
	}
}
