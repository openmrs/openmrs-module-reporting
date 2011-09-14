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

import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PatientIdDataDefinition to produce a PatientData
 */
@Handler(supports=PatientIdDataDefinition.class, order=50)
public class PatientIdDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData c = new EvaluatedPatientData(definition, context);
		if (context.getBaseCohort() != null) {
			for (Integer pId : context.getBaseCohort().getMemberIds()) {
				c.addData(pId, pId);
			}
		}
		else {
			DataSetQueryService qs = Context.getService(DataSetQueryService.class);		
			Map<Integer, Object> data = qs.getPropertyValues(Patient.class, "patientId", context);
			c.setData(data);
		}
		return c;
	}
}
