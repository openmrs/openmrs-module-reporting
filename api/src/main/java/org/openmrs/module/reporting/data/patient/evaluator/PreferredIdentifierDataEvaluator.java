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
		
		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getIdentifierType() == null) {
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

		Map<Integer, Object> m = Context.getService(EvaluationService.class).evaluateToMap(q, Integer.class, Object.class);
		c.setData(m);

		return c;
	}
}
