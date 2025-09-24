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

import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates an ProgramEnrollmentsForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=ProgramEnrollmentsForPatientDataDefinition.class, order=50)
public class ProgramEnrollmentsForPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return patient programs that are active on a given date
	 * @should return patient programs started on or after a given date
	 * @should return patient programs started on or before a given date
	 * @should return patient programs completed on or after a given date
	 * @should return patient programs completed on or before a given date
	 * @should return the first patient program by enrollment date
	 * @should return the last patient program by enrollment date
	 * @should return a list of patient programs for each patient 
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		ProgramEnrollmentsForPatientDataDefinition def = (ProgramEnrollmentsForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pp.patient.patientId", "pp");
		q.from(PatientProgram.class, "pp");
		q.wherePatientIn("pp.patient.patientId", context);
		q.whereEqual("pp.program", def.getProgram());
		q.whereGreaterOrEqualTo("pp.dateEnrolled", def.getEnrolledOnOrAfter());
		q.whereLessOrEqualTo("pp.dateEnrolled", def.getEnrolledOnOrBefore());
		q.whereGreaterOrEqualTo("pp.dateCompleted", def.getCompletedOnOrAfter());
		q.whereLessOrEqualTo("pp.dateCompleted", def.getCompletedOnOrBefore());

		if (def.getActiveOnDate() != null) {
			q.whereLessOrEqualTo("pp.dateEnrolled", def.getActiveOnDate());
			q.whereGreaterOrNull("pp.dateCompleted", def.getActiveOnDate());
		}

		if (def.getWhichEnrollment() == TimeQualifier.LAST) {
			q.orderDesc("pp.dateEnrolled");
		}
		else {
			q.orderAsc("pp.dateEnrolled");
		}
		
		List<Object[]> queryResult = evaluationService.evaluateToList(q, context);
		
		ListMap<Integer, PatientProgram> enrollmentsForPatients = new ListMap<Integer, PatientProgram>();
		for (Object[] row : queryResult) {
			enrollmentsForPatients.putInList((Integer)row[0], (PatientProgram)row[1]);
		}
		
		for (Integer pId : enrollmentsForPatients.keySet()) {
			List<PatientProgram> l = enrollmentsForPatients.get(pId);
			if (def.getDataType() == PatientProgram.class) {
				c.addData(pId, l.get(0));
			}
			else {
				c.addData(pId, l);
			}
		}
		
		return c;
	}
}
