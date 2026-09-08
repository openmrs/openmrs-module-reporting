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

		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(data);

		return c;
	}
}
