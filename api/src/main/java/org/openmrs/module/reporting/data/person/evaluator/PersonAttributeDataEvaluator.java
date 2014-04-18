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

import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PersonAttributeDataDefinition to produce a PatientData
 */
@Handler(supports=PersonAttributeDataDefinition.class, order=50)
public class PersonAttributeDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return the person attribute of the passed type for each person in the passed context
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		PersonAttributeDataDefinition def = (PersonAttributeDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);
		
		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getPersonAttributeType() == null) {
			return c;
		}
		
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pa.person.personId", "pa");
		q.from(PersonAttribute.class, "pa");
		q.wherePersonIn("pa.person.personId", context);
		q.whereEqual("pa.attributeType", def.getPersonAttributeType());

		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class);
		c.setData(data);

		return c;
	}
}
