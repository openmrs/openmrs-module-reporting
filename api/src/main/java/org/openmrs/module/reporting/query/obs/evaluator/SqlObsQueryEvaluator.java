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
package org.openmrs.module.reporting.query.obs.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.definition.SqlObsQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The logic that evaluates a {@link SqlObsQuery} and produces an {@link Query}
 */
@Handler(supports=SqlObsQuery.class)
public class SqlObsQueryEvaluator implements ObsQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * Public constructor
	 */
	public SqlObsQueryEvaluator() { }
	
	/**
	 * @see ObsQueryEvaluator#evaluate(ObsQuery, EvaluationContext)
	 * @should evaluate a SQL query into an ObsQuery
	 * @should filter results given a base Obs Query Result in an EvaluationContext
	 * @should filter results given a base Encounter Query Result in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SqlObsQuery sqlObsQuery = (SqlObsQuery) definition;
		ObsQueryResult queryResult = new ObsQueryResult(sqlObsQuery, context);

		ObsIdSet obsIds = new ObsIdSet(ObsDataUtil.getObsIdsForContext(context, false));
		if (obsIds.getSize() == 0) {
			return queryResult;
		}

		SqlQueryBuilder qb = new SqlQueryBuilder();
		qb.append(sqlObsQuery.getQuery());
		qb.setParameters(context.getParameterValues());

		if (sqlObsQuery.getQuery().contains(":obsIds")) {
			qb.addParameter("obsIds", obsIds);
		}

		List<Integer> l = evaluationService.evaluateToList(qb, Integer.class, context);
		l.retainAll(obsIds.getMemberIds());
		queryResult.addAll(l);

		return queryResult;
	}
}
