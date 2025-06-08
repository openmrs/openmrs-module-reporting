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
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PersonToPatientDataDefinition to produce a PatientData
 */
@Handler(supports=PersonToPatientDataDefinition.class, order=50)
public class PersonToPatientDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return person data by for each patient in the passed cohort
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData c = new EvaluatedPatientData(definition, context);
		PersonToPatientDataDefinition def = (PersonToPatientDataDefinition)definition;
		EvaluatedPersonData personData = Context.getService(PersonDataService.class).evaluate(def.getJoinedDefinition(), context);
		c.setData(personData.getData());
		return c;
	}
}
