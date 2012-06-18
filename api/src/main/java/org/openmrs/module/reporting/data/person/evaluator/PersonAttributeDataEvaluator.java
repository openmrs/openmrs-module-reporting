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
package org.openmrs.module.reporting.data.person.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PersonAttributeDataDefinition to produce a PatientData
 */
@Handler(supports=PersonAttributeDataDefinition.class, order=50)
public class PersonAttributeDataEvaluator implements PersonDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the person attribute of the passed type for each person in the passed context
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		PersonAttributeDataDefinition def = (PersonAttributeDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);
		
		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getPersonAttributeType() == null) {
			return c;
		}
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		hql.append("from 		PersonAttribute ");
		hql.append("where 		voided = false ");
		if (context.getBaseCohort() != null) {
			hql.append("and 		person.personId in (:patientIds) ");
		}
		hql.append("and 		attributeType.personAttributeTypeId = :idType ");
		Map<String, Object> m = new HashMap<String, Object>();
		if (context.getBaseCohort() != null) {
			m.put("patientIds", context.getBaseCohort());
		}
		m.put("idType", def.getPersonAttributeType().getPersonAttributeTypeId());
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		for (Object o : queryResult) {
			PersonAttribute pa = (PersonAttribute)o;
			c.addData(pa.getPerson().getPersonId(), pa);  // TODO: This is probably inefficient.  Try to improve this with HQL
		}
		return c;
	}
}
