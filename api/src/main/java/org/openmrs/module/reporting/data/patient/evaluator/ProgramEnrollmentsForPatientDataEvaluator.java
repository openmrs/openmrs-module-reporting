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
		
		List<Object[]> queryResult = evaluationService.evaluateToList(q);
		
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
