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
