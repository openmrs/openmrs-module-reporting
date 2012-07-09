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
		
		hql.append("from 		PatientState ");
		hql.append("where 		voided = false ");
		
		hql.append("and 		state.programWorkflowStateId = :stateId ");
		m.put("stateId", def.getState().getProgramWorkflowStateId());
		
		if (context.getBaseCohort() != null) {
			hql.append("and 	patientProgram.patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		if (def.getStartedOnOrBefore() != null) {
			hql.append("and		startDate <= :startedOnOrBefore ");
			m.put("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getStartedOnOrBefore()));
		}
		
		if (def.getStartedOnOrAfter() != null) {
			hql.append("and		startDate >= :startedOnOrAfter ");
			m.put("startedOnOrAfter", def.getStartedOnOrAfter());
		}
		
		if (def.getEndedOnOrBefore() != null) {
			hql.append("and		endDate <= :endedOnOrBefore ");
			m.put("endedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getEndedOnOrBefore()));
		}
		
		if (def.getEndedOnOrAfter() != null) {
			hql.append("and		endDate >= :endedOnOrAfter ");
			m.put("endedOnOrAfter", def.getEndedOnOrAfter());
		}
		
		if (def.getActiveOnDate() != null) {
			hql.append("and		startDate <= :startedOnOrBefore ");
			hql.append("and		(endDate is null or endDate >= :endedOnOrAfter) ");
			m.put("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getActiveOnDate()));
			m.put("endedOnOrAfter", def.getActiveOnDate());
		}
		
		hql.append("order by 	startDate " + (def.getWhich() == TimeQualifier.LAST ? "desc" : "asc"));
		
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		
		ListMap<Integer, PatientState> statesForPatients = new ListMap<Integer, PatientState>();
		for (Object o : queryResult) {
			PatientState ps = (PatientState)o;
			statesForPatients.putInList(ps.getPatientProgram().getPatient().getPatientId(), ps); // TODO: Make this more efficient via HQL
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
