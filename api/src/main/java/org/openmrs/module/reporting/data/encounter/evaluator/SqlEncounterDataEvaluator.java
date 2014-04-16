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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

/**
 * Expects that the SQL query returns two columns, an Integer
 */
@Handler(supports=SqlEncounterDataDefinition.class)
public class SqlEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition def, EvaluationContext context) throws EvaluationException {
        SqlEncounterDataDefinition definition = (SqlEncounterDataDefinition) def;
        EvaluatedEncounterData data = new EvaluatedEncounterData(definition, context);

		// TODO: Support IdSetMember joining

        Set<Integer> encounterIds = EncounterDataUtil.getEncounterIdsForContext(context, false);
        if (encounterIds.size() == 0) {
            return data;
        }

		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append(definition.getSql());
		for (Parameter p : definition.getParameters()) {
			q.addParameter(p.getName(), context.getParameterValue(p.getName()));
		}
		q.addParameter("encounterIds", encounterIds);

		Map<Integer, Object> results = evaluationService.evaluateToMap(q, Integer.class, Object.class);
		data.setData(results);

        return data;
    }

}
