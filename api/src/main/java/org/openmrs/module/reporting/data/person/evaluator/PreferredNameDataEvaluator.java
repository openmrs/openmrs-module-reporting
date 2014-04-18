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

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PreferredNameDataDefinition to produce a PersonData
 */
@Handler(supports = PreferredNameDataDefinition.class, order = 50)
public class PreferredNameDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/**
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 *
	 * @should return the most preferred name for each person in the passed context
	 * @should return empty result set for an empty base cohort
	 * @should return the preferred name for all persons
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pn.person.personId", "pn");
		q.from(PersonName.class, "pn");
		q.wherePersonIn("pn.person.personId", context);
		q.orderAsc("pn.preferred");

		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class);
		c.setData(data);

		return c;
	}
}
