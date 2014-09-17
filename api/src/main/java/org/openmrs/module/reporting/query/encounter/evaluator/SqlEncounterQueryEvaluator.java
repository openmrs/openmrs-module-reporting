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
