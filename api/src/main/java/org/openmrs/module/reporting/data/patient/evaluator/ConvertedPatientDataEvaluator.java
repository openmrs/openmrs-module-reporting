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
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a ConvertedPatientDataDefinition
 */
@Handler(supports=ConvertedPatientDataDefinition.class, order=50)
public class ConvertedPatientDataEvaluator implements PatientDataEvaluator {

	/**
	 * @see org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return person data by for each patient in the passed cohort
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData c = new EvaluatedPatientData(definition, context);
		ConvertedPatientDataDefinition def = (ConvertedPatientDataDefinition)definition;
		EvaluatedPatientData unconvertedData = Context.getService(PatientDataService.class).evaluate(def.getDefinitionToConvert(), context);
		if (def.getConverters().isEmpty()) {
			c.setData(unconvertedData.getData());
		}
		else {
			for (Integer id : unconvertedData.getData().keySet()) {
				Object val = DataUtil.convertData(unconvertedData.getData().get(id), def.getConverters());
				c.addData(id, val);
			}
		}
		return c;
	}
}
