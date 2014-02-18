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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

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
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("select		pi.patient.patientId, pi ");
		hql.append("from		PatientIdentifier as pi ");
		hql.append("where 		voided = false ");
		hql.append("and 		pi.identifierType = :idType ");
		m.put("idType", def.getIdentifierType());

		if (context.getBaseCohort() != null) {
			hql.append("and 	pi.patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}

		if (def.getLocation() != null) {
			hql.append("and 	pi.location = :location) ");
			m.put("location", def.getLocation());
		}

		// Order to ensure that the preferred is based on the preferred flag first, dateCreated second
		hql.append("order by 	pi.preferred asc, pi.dateCreated asc");

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			c.addData((Integer)parts[0], parts[1]);
		}
		return c;
	}
}
