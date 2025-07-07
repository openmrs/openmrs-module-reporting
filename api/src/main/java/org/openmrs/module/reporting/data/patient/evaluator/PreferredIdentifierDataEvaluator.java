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

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;

import java.util.Map;

/**
 * Evaluates a PreferredIdentifierDataDefinition to produce a PatientData
 */
@Handler(supports=PreferredIdentifierDataDefinition.class, order=50)
public class PreferredIdentifierDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return the preferred identifier of the passed type for each patient in the passed context
	 * @should limit the returned identifier to the configured location if set
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		PreferredIdentifierDataDefinition def = (PreferredIdentifierDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pi.patient.patientId", "pi");
		q.from(PatientIdentifier.class, "pi");
		q.whereEqual("pi.identifierType", def.getIdentifierType());
		q.whereEqual("pi.location", def.getLocation());
		q.wherePatientIn("pi.patient.patientId", context);

		// Order to ensure that the preferred is based on the preferred flag first, dateCreated second
		q.orderAsc("pi.preferred").orderAsc("pi.dateCreated");

		Map<Integer, Object> m = Context.getService(EvaluationService.class).evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(m);

		return c;
	}
}
