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

import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Evaluates a VitalStatusDataDefinition to produce a PersonData
 */
@Handler(supports=VitalStatusDataDefinition.class, order=50)
public class VitalStatusDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the vital status by person
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personId", "p.dead", "p.deathDate", "cod");
		q.from(Person.class, "p");
		q.leftOuterJoin("p.causeOfDeath", "cod");
		q.wherePersonIn("p.personId", context);

		List<Object[]> results = evaluationService.evaluateToList(q, context);
		for (Object[] row : results) {
			Integer pId = (Integer)row[0];
			boolean dead = (row[1] == Boolean.TRUE);
			Date deathDate = (dead ? (Date)row[2] : null);
			Concept causeOfDeath = (dead ? (Concept)row[3] : null);
			c.addData(pId, new VitalStatus(dead, deathDate, causeOfDeath));
		}

		return c;
	}
}
