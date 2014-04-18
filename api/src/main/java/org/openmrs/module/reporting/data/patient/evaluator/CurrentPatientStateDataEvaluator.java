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
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.CurrentPatientStateDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates a CurrentPatientStateDataDefinition to produce a PatientData
 */
@Handler(supports=CurrentPatientStateDataDefinition.class, order=50)
public class CurrentPatientStateDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

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

		Date effectiveDate = ObjectUtil.nvl(def.getEffectiveDate(), new Date());

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("ps.patientProgram.patient.patientId", "ps");
		q.from(PatientState.class, "ps");
		q.whereLessOrEqualToOrNull("ps.startDate", effectiveDate);
		q.whereGreaterOrNull("ps.endDate", effectiveDate);
		q.whereEqual("ps.state.programWorkflow", def.getWorkflow());
		q.wherePatientIn("ps.patientProgram.patient.patientId", context);
		q.orderAsc("ps.startDate");

		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class);
		c.setData(data);

		return c;
	}
}
