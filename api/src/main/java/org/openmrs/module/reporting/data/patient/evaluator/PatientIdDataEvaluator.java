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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PatientIdDataDefinition to produce a PatientData
 */
@Handler(supports=PatientIdDataDefinition.class, order=50)
public class PatientIdDataEvaluator extends PatientPropertyDataEvaluator {

	@Override
	public String getPropertyName() {
		return "patientId";
	}

	/**
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return patientIds for all patients in the the passed context
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData c = new EvaluatedPatientData(definition, context);
		if (context.getBaseCohort() == null) {
			return super.evaluate(definition, context);
		}
		for (Integer pId : context.getBaseCohort().getMemberIds()) {
			c.addData(pId, pId);
		}
		return c;
	}
}
