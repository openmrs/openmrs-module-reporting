/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.person.PersonDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.openmrs.module.reporting.query.person.definition.SqlPersonQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The logic that evaluates a {@link SqlPersonQuery} and produces an {@link Query}
 */
@Handler(supports=SqlPersonQuery.class)
public class SqlPersonQueryEvaluator implements PersonQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private EvaluationService evaluationService;
	
	/**
	 * @see PersonQueryEvaluator#evaluate(PersonQuery, EvaluationContext)
	 * @should evaluate a SQL query into an PersonQuery
	 * @should filter results given a base Person Query Result in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public PersonQueryResult evaluate(PersonQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SqlPersonQuery sqlPersonQuery = (SqlPersonQuery) definition;
		PersonQueryResult queryResult = new PersonQueryResult(sqlPersonQuery, context);

		PersonIdSet personIds = new PersonIdSet(PersonDataUtil.getPersonIdsForContext(context, false));

		if (personIds.getSize() == 0) {
			return queryResult;
		}

		SqlQueryBuilder qb = new SqlQueryBuilder();
		qb.append(sqlPersonQuery.getQuery());
		qb.setParameters(context.getParameterValues());

		if (sqlPersonQuery.getQuery().contains(":personIds")) {
			qb.addParameter("personIds", personIds);
		}

		if (sqlPersonQuery.getQuery().contains(":patientIds")) {
			qb.addParameter("patientIds", personIds);
		}

		List<Integer> l = evaluationService.evaluateToList(qb, Integer.class, context);
		if (personIds != null) {
			l.retainAll(personIds.getMemberIds());
		}
		queryResult.addAll(l);

		return queryResult;
	}
}
