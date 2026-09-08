/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
