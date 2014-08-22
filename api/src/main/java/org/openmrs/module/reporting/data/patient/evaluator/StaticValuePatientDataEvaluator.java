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

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.StaticValuePatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates a StaticValuePatientDataDefinition to produce a PatientData
 */
@Handler(supports=StaticValuePatientDataDefinition.class, order=50)
public class StaticValuePatientDataEvaluator implements PatientDataEvaluator  {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	/**
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return configured static value for all patients in the the passed context
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData ret = new EvaluatedPatientData(definition, context);
		StaticValuePatientDataDefinition cd = (StaticValuePatientDataDefinition)definition;
		Cohort cohort = cohortDefinitionService.evaluate(new AllPatientsCohortDefinition(), context);
		for (Integer pId : cohort.getMemberIds()) {
			ret.addData(pId, cd.getStaticValue());
		}
		return ret;
	}
}
