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

		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(data);

		return c;
	}
}
