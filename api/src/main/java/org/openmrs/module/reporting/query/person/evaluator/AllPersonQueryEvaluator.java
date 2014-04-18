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
package org.openmrs.module.reporting.query.person.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.AllPersonQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The logic that evaluates a {@link AllPersonQuery} and produces an {@link PersonQueryResult}
 */
@Handler(supports=AllPersonQuery.class)
public class AllPersonQueryEvaluator implements PersonQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * Public constructor
	 */
	public AllPersonQueryEvaluator() { }
	
	/**
	 * @see PersonQueryEvaluator#evaluate(PersonQuery, EvaluationContext)
	 * @should return all of the person ids for all patients in the defined query
	 * @should filter results by patient and person given an PersonEvaluationContext
	 * @should filter results by patient given an EvaluationContext
	 */
	public PersonQueryResult evaluate(PersonQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		AllPersonQuery query = (AllPersonQuery) definition;
		PersonQueryResult result = new PersonQueryResult(query, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return result;
		}
		if (context instanceof PersonEvaluationContext) {
			PersonIdSet ids = ((PersonEvaluationContext) context).getBasePersons();
			if (ids != null && ids.getMemberIds().isEmpty()) {
				return result;
			}
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("p.personId");
		q.from(Person.class, "p");
		q.wherePersonIn("p.personId", context);

		List<Integer> results = evaluationService.evaluateToList(q, Integer.class);
		result.getMemberIds().addAll(results);

		return result;
	}
}
