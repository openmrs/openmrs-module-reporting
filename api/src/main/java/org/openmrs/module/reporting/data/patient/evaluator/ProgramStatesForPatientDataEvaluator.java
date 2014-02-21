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
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates an ProgramStatesForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=ProgramStatesForPatientDataDefinition.class, order=50)
public class ProgramStatesForPatientDataEvaluator implements PatientDataEvaluator {

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
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("select 		ps.patientProgram.patient.patientId, ps ");
		hql.append("from 		PatientState as ps ");
		hql.append("where 		ps.voided = false ");
		if (context.getBaseCohort() != null) {
			hql.append("and 	ps.patientProgram.patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		if (def.getWorkflow() != null) {
			hql.append("and 	ps.state.programWorkflow = :workflow ");
			m.put("workflow", def.getWorkflow());
		}
		if (def.getState() != null) {
			hql.append("and 	ps.state = :state ");
			m.put("state", def.getState());
		}
		if (def.getLocation() != null) {
			hql.append("and 	ps.patientProgram.location = :location ");
			m.put("location", def.getLocation());
		}
		if (def.getStartedOnOrBefore() != null) {
			hql.append("and		ps.startDate <= :startedOnOrBefore ");
			m.put("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getStartedOnOrBefore()));
		}
		if (def.getStartedOnOrAfter() != null) {
			hql.append("and		ps.startDate >= :startedOnOrAfter ");
			m.put("startedOnOrAfter", def.getStartedOnOrAfter());
		}
		if (def.getEndedOnOrBefore() != null) {
			hql.append("and		ps.endDate <= :endedOnOrBefore ");
			m.put("endedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getEndedOnOrBefore()));
		}
		if (def.getEndedOnOrAfter() != null) {
			hql.append("and		ps.endDate >= :endedOnOrAfter ");
			m.put("endedOnOrAfter", def.getEndedOnOrAfter());
		}
		if (def.getActiveOnDate() != null) {
			hql.append("and		ps.startDate <= :startedOnOrBefore ");
			hql.append("and		(ps.endDate is null or ps.endDate >= :endedOnOrAfter) ");
			m.put("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getActiveOnDate()));
			m.put("endedOnOrAfter", def.getActiveOnDate());
		}
		
		hql.append("order by 	ps.startDate " + (def.getWhich() == TimeQualifier.LAST ? "desc" : "asc"));
		
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		ListMap<Integer, PatientState> statesForPatients = new ListMap<Integer, PatientState>();
		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				PatientState pi = (PatientState) parts[1];
				statesForPatients.putInList(pId, pi);
			}
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
