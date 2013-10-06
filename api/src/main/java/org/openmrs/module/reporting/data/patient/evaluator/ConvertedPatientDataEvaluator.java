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
