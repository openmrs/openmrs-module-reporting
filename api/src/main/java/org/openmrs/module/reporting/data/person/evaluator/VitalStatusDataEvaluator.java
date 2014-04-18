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

		List<Object[]> results = evaluationService.evaluateToList(q);
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
