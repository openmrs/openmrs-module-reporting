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

package org.openmrs.module.reporting.data.encounter.evaluator;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * Expects that the SQL query returns two columns, an Integer
 */
@Handler(supports=SqlEncounterDataDefinition.class)
public class SqlEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition def, EvaluationContext ctx) throws EvaluationException {
        SqlEncounterDataDefinition definition = (SqlEncounterDataDefinition) def;
        EncounterEvaluationContext context = (EncounterEvaluationContext) ctx;

        EvaluatedEncounterData data = new EvaluatedEncounterData(definition, context);

        Set<Integer> encounterIds = EncounterDataUtil.getEncounterIdsForContext(context, false);
        if (encounterIds.size() == 0) {
            return data;
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(definition.getSql());
        query.setParameterList("encounterIds", encounterIds);

        DataUtil.populate(data, (List<Object[]>) query.list());
        return data;
    }

}
