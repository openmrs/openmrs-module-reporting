/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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

		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(data);

		return c;
	}
}
