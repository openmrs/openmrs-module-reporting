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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.visit.VisitDataUtil;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.AllVisitQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;

/**
 * The logic that evaluates a {@link AllVisitQuery} and produces an {@link Query}
 */
@Handler(supports=AllVisitQuery.class)
public class AllVisitQueryEvaluator implements VisitQueryEvaluator {

    /**
     * @see VisitQueryEvaluator#evaluate(Definition, EvaluationContext)
     * @should return all of the visit ids for all patients in the defined query
     * @should filter results by patient and visit given an VisitEvaluationContext
     * @should filter results by patient given an EvaluationContext
     */
    public VisitQueryResult evaluate(VisitQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        VisitQueryResult queryResult = new VisitQueryResult(definition, context);
        queryResult.setMemberIds(VisitDataUtil.getVisitIdsForContext(context, false));
        return queryResult;
    }
}
