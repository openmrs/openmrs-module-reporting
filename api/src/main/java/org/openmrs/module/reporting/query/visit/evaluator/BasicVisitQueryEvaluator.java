/*
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

package org.openmrs.module.reporting.query.visit.evaluator;

import java.util.List;

import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.BasicVisitQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = BasicVisitQuery.class)
public class BasicVisitQueryEvaluator implements VisitQueryEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Override
	public VisitQueryResult evaluate(VisitQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());

		BasicVisitQuery query = (BasicVisitQuery) definition;
		VisitQueryResult result = new VisitQueryResult(query, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return result;
		}
		if (context instanceof VisitEvaluationContext) {
			VisitIdSet baseVisits = ((VisitEvaluationContext) context).getBaseVisits();
			if (baseVisits != null && baseVisits.getMemberIds().isEmpty()) {
				return result;
			}
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("v.visitId");
		q.from(Visit.class, "v");
		q.whereIn("v.visitType", query.getVisitTypes());
		q.whereGreaterOrEqualTo("v.startDatetime", query.getStartedOnOrAfter());
		q.whereLessOrEqualTo("v.startDatetime", query.getStartedOnOrBefore());
		q.whereGreaterOrEqualTo("v.stopDatetime", query.getEndedOnOrAfter());
		q.whereLessOrEqualTo("v.stopDatetime", query.getEndedOnOrBefore());
		q.whereIn("v.location", query.getLocationList());
		q.whereVisitIn("v.visitId", context);

		for (Object[] row : evaluationService.evaluateToList(q, context)) {
			result.add((Integer)row[0]);
		}

		return result;
	}

}
