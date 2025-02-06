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

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientObjectDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a Patient to produce a PatientData
 */
@Handler(supports={PatientObjectDataDefinition.class})
public class PatientObjectDataEvaluator implements PatientDataEvaluator {

	@Autowired
	private EvaluationService evaluationService;

	/**
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return patient objects given the passed context
	 */
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData c = new EvaluatedPatientData(definition, context);
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.patientId, p");
		q.from(Patient.class, "p");
		q.wherePatientIn("p.patientId", context);
		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(data);
		return c;
	}
}
