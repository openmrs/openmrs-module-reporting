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
