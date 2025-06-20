/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.evaluator;

import org.openmrs.Visit;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a VisitDatetimeDataDefinition to produce a VisitData
 */
public abstract class VisitPropertyDataEvaluator implements VisitDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public abstract String getPropertyName();

    /**
     * @see org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator#evaluate(org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * @should return all visit datetimes given the passed context
     */
    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedVisitData c = new EvaluatedVisitData(definition, context);
        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("v.visitId", "v."+getPropertyName());
        q.from(Visit.class, "v");
        q.whereVisitIn("v.visitId", context);
        Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
