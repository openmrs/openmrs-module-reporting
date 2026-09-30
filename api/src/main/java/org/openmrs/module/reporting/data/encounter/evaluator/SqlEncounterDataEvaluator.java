/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Expects that the SQL query returns two columns:
 *   the first should be an Integer returning the encounterId
 *   the second should be the data you wish to retrieve for each Encounter
 * Expects that you use "encounterIds" within your query to limit by the base id set in the evaluation context:
 *   eg. "select encounter_datetime from encounter where encounter_id in (:encounterIds)"
 */
@Handler(supports=SqlEncounterDataDefinition.class)
public class SqlEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition def, EvaluationContext context) throws EvaluationException {
        SqlEncounterDataDefinition definition = (SqlEncounterDataDefinition) def;
        EvaluatedEncounterData data = new EvaluatedEncounterData(definition, context);

        EncounterIdSet encounterIds = new EncounterIdSet(EncounterDataUtil.getEncounterIdsForContext(context, false));
        if (encounterIds.getSize() == 0) {
            return data;
        }

		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append(definition.getSql());
		for (Parameter p : definition.getParameters()) {
			q.addParameter(p.getName(), context.getParameterValue(p.getName()));
		}
		q.addParameter("encounterIds", encounterIds);

		Map<Integer, Object> results = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		data.setData(results);

        return data;
    }

}
