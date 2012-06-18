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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.CurrentPatientStateDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a CurrentPatientStateDataDefinition to produce a PatientData
 */
@Handler(supports=CurrentPatientStateDataDefinition.class, order=50)
public class CurrentPatientStateDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return the current state of the configured workflow for each patient in the passed context
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		CurrentPatientStateDataDefinition def = (CurrentPatientStateDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getWorkflow() == null) {
			return c;
		}
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from 		PatientState ");
		hql.append("where 		voided = false ");
		if (context.getBaseCohort() != null) {
			hql.append("and 		patientProgram.patient.patientId in (:patientIds) ");
		}
		hql.append("and 		state.programWorkflow.programWorkflowId = :workflowId ");
		hql.append("and 		(startDate is null or startDate <= :effectiveDate )");
		hql.append("and 		(endDate is null or endDate > :effectiveDate) ");
		hql.append("order by	startDate asc ");

		if (context.getBaseCohort() != null) {
			m.put("patientIds", context.getBaseCohort());
		}
		m.put("workflowId", def.getWorkflow().getProgramWorkflowId());
		m.put("effectiveDate", ObjectUtil.nvl(def.getEffectiveDate(), new Date()));
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		for (Object o : queryResult) {
			PatientState ps = (PatientState)o;
			c.addData(ps.getPatientProgram().getPatient().getPatientId(), ps);  // TODO: This is probably inefficient.  Try to improve this with HQL
		}
		return c;
	}
}
