/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.visit.evaluator;

import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.ActiveVisitQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * The logic that evaluates an {@link ActiveVisitQuery} and produces a {@link VisitQueryResult}
 */
@Handler(supports=ActiveVisitQuery.class)
public class ActiveVisitQueryEvaluator implements VisitQueryEvaluator {

    @Autowired
    EvaluationService evaluationService;

    public VisitQueryResult evaluate(VisitQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        VisitQueryResult queryResult = new VisitQueryResult(definition, context);

        ActiveVisitQuery query = (ActiveVisitQuery) definition;
        Date asOfDate = query.getAsOfDate() != null ? query.getAsOfDate() : new Date();

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("v.visitId");
        q.from(Visit.class, "v");
        q.whereLessOrEqualTo("v.startDatetime", asOfDate);
        q.whereGreaterEqualOrNull("v.stopDatetime", asOfDate);
        q.whereVisitIn("v.visitId", context);

        List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
        queryResult.getMemberIds().addAll(results);
        return queryResult;
    }

}
