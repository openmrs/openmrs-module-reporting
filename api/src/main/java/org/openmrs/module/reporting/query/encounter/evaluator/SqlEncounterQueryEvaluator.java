/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The logic that evaluates a {@link SqlEncounterQuery} and produces an {@link EncounterQueryResult}
 */
@Handler(supports=SqlEncounterQuery.class)
public class SqlEncounterQueryEvaluator implements EncounterQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private EvaluationService evaluationService;
	
	/**
	 * Public constructor
	 */
	public SqlEncounterQueryEvaluator() { }
	
	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should evaluate a SQL query into an EncounterQuery
	 * @should filter results given a base Encounter Query Result in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SqlEncounterQuery sqlEncounterQuery = (SqlEncounterQuery) definition;
		EncounterQueryResult queryResult = new EncounterQueryResult(sqlEncounterQuery, context);

		EncounterIdSet encounterIds = new EncounterIdSet(EncounterDataUtil.getEncounterIdsForContext(context, false));
		if (encounterIds.getSize() == 0) {
			return queryResult;
		}

		SqlQueryBuilder qb = new SqlQueryBuilder();
		qb.append(sqlEncounterQuery.getQuery());
		qb.setParameters(context.getParameterValues());

		if (sqlEncounterQuery.getQuery().contains(":encounterIds")) {
			qb.addParameter("encounterIds", encounterIds);
		}

		List<Integer> l = evaluationService.evaluateToList(qb, Integer.class, context);
		l.retainAll(encounterIds.getMemberIds());
		queryResult.addAll(l);

		return queryResult;
	}
}
