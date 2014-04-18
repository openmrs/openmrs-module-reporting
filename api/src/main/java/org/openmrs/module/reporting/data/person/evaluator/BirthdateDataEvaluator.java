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

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Evaluates a BirthdateDataDefinition to produce a PersonData
 */
@Handler(supports=BirthdateDataDefinition.class, order=50)
public class BirthdateDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return all birth dates for all persons
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personId", "p.birthdate", "p.birthdateEstimated");
		q.from(Person.class, "p");
		q.wherePersonIn("p.personId", context);

		List<Object[]> results = evaluationService.evaluateToList(q);
		for (Object[] row : results) {
			Integer pId = (Integer)row[0];
			Date birthdate = (Date)row[1];
			boolean estimated = (row[2] == Boolean.TRUE);
			if (birthdate != null) {
				c.addData(pId, new Birthdate(birthdate, estimated));
			}
			else {
				c.addData(pId, null);
			}
		}

		return c;
	}
}
