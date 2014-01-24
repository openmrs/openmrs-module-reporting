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

package org.openmrs.module.reporting.query.encounter.evaluator;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = BasicEncounterQuery.class)
public class BasicEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());

        BasicEncounterQuery query = (BasicEncounterQuery) definition;
        EncounterQueryResult result = new EncounterQueryResult(query, context);

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
        criteria.setProjection(Projections.id());
        criteria.add(Restrictions.eq("voided", false));

        if (query.getOnOrAfter() != null) {
            criteria.add(Restrictions.ge("encounterDatetime", query.getOnOrAfter()));
        }
        if (query.getOnOrBefore() != null) {
            criteria.add(Restrictions.le("encounterDatetime", DateUtil.getEndOfDayIfTimeExcluded(query.getOnOrBefore())));
        }

        if (context.getBaseCohort() != null) {
            if (context.getBaseCohort().size() == 0) {
                return result;
            } else {
                criteria.add(Restrictions.in("patient.id", context.getBaseCohort().getMemberIds()));
            }
        }
        if (context instanceof EncounterEvaluationContext) {
            EncounterIdSet baseEncounters = ((EncounterEvaluationContext) context).getBaseEncounters();
            if (baseEncounters != null) {
                if (baseEncounters.getSize() == 0) {
                    return result;
                } else {
                    criteria.add(Restrictions.in("id", baseEncounters.getMemberIds()));
                }
            }
        }

        for (Integer encounterId : ((List<Integer>) criteria.list())) {
            result.add(encounterId);
        }
        return result;
    }
}
