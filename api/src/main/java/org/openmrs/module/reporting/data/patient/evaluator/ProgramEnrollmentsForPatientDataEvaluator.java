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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an ProgramEnrollmentsForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=ProgramEnrollmentsForPatientDataDefinition.class, order=50)
public class ProgramEnrollmentsForPatientDataEvaluator implements PatientDataEvaluator {

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
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from 		PatientProgram ");
		hql.append("where 		voided = false ");
		
		hql.append("and 		program.programId = :programId ");
		m.put("programId", def.getProgram().getProgramId());
		
		if (context.getBaseCohort() != null) {
			hql.append("and 	patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		if (def.getEnrolledOnOrBefore() != null) {
			hql.append("and		dateEnrolled <= :enrolledOnOrBefore ");
			m.put("enrolledOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getEnrolledOnOrBefore()));
		}
		
		if (def.getEnrolledOnOrAfter() != null) {
			hql.append("and		dateEnrolled >= :enrolledOnOrAfter ");
			m.put("enrolledOnOrAfter", def.getEnrolledOnOrAfter());
		}
		
		if (def.getCompletedOnOrBefore() != null) {
			hql.append("and		dateCompleted <= :completedOnOrBefore ");
			m.put("completedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getCompletedOnOrBefore()));
		}
		
		if (def.getCompletedOnOrAfter() != null) {
			hql.append("and		dateCompleted >= :completedOnOrAfter ");
			m.put("completedOnOrAfter", def.getCompletedOnOrAfter());
		}
		
		if (def.getActiveOnDate() != null) {
			hql.append("and		dateEnrolled <= :enrolledOnOrBefore ");
			hql.append("and		(dateCompleted is null or dateCompleted >= :completedOnOrAfter) ");
			m.put("enrolledOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getActiveOnDate()));
			m.put("completedOnOrAfter", def.getActiveOnDate());
		}
		
		hql.append("order by 	dateEnrolled " + (def.getWhichEnrollment() == TimeQualifier.LAST ? "desc" : "asc"));
		
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		
		ListMap<Integer, PatientProgram> enrollmentsForPatients = new ListMap<Integer, PatientProgram>();
		for (Object o : queryResult) {
			PatientProgram pp = (PatientProgram)o;
			enrollmentsForPatients.putInList(pp.getPatient().getPatientId(), pp); // TODO: Make this more efficient via HQL
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
