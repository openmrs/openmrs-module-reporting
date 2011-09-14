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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PatientIdentifierDataDefinition to produce a PatientData
 */
@Handler(supports=PatientIdentifierDataDefinition.class, order=50)
public class PatientIdentifierDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		PatientIdentifierDataDefinition def = (PatientIdentifierDataDefinition)definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		StringBuilder query = new StringBuilder();
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("typeId", def.getType().getPatientIdentifierTypeId());
		
		query.append("select 	pi.patient.patientId, pi.identifier ");
		query.append("from 		PatientIdentifier pi ");
		query.append("where 	pi.voided = false ");
		query.append("and		pi.identifierType.id = :typeId ");
		if (context.getBaseCohort() != null) {
			query.append("and	pi.patient.patientId in (:patientIds) ");
			parameterValues.put("patientIds", context.getBaseCohort().getMemberIds());
		}
		query.append("order by	pi.preferred asc "); // This should make preferred = 1 come up last, which will put it in the return set
		
		List<Object> l = Context.getService(DataSetQueryService.class).executeHqlQuery(query.toString(), parameterValues);
		for (Object o : l) {
			Object[] row = (Object[])o;
			c.addData((Integer)row[0], (String)row[1]);
		}
		return c;
	}
}
