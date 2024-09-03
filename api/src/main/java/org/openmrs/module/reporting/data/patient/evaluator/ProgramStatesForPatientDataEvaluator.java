/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
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

		List<Object[]> queryResult = evaluationService.evaluateToList(q, context);

		ListMap<Integer, PatientState> statesForPatients = new ListMap<Integer, PatientState>();
		for (Object[] row : queryResult) {
			statesForPatients.putInList((Integer)row[0], (PatientState)row[1]);
		}
		
		for (Integer pId : statesForPatients.keySet()) {
			List<PatientState> l = statesForPatients.get(pId);
			Collections.sort(l, new PatientStateComparator());
			if (def.getDataType() == PatientState.class) {
				PatientState ps = (def.getWhich() == TimeQualifier.LAST ? l.get(l.size()-1) : l.get(0));
				c.addData(pId, ps);
			}
			else {
				c.addData(pId, l);
			}
		}
		
		return c;
	}

    /**
     * This is necessary because the PatientState comparison function does not account for program enrollment dates
     */
    private class PatientStateComparator implements Comparator<PatientState> {
        @Override
        public int compare(PatientState s1, PatientState s2) {
            int result = OpenmrsUtil.compareWithNullAsEarliest(s1.getStartDate(), s2.getStartDate());
            if (result == 0) {
                result = OpenmrsUtil.compareWithNullAsLatest(s1.getEndDate(), s2.getEndDate());
            }
            if (result == 0) {
                result = OpenmrsUtil.compareWithNullAsEarliest(s1.getPatientProgram().getDateEnrolled(), s2.getPatientProgram().getDateEnrolled());
            }
            if (result == 0) {
                result = OpenmrsUtil.compareWithNullAsLatest(s1.getPatientProgram().getDateCompleted(), s2.getPatientProgram().getDateCompleted());
            }
            if (result == 0) {
                result = OpenmrsUtil.compareWithNullAsGreatest(s1.getUuid(), s2.getUuid());
            }
            return result;
        }
    }
}
