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

import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.VisitDataUtil;
import org.openmrs.module.reporting.data.visit.definition.SqlVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Expects that the SQL query returns two columns:
 *   the first should be an Integer returning the visitId
 *   the second should be the data you wish to retrieve for each Visit
 * Expects that you use "visitIds" within your query to limit by the base id set in the evaluation context:
 *   eg. "select start_datetime from visit where visit_id in (:visitIds)"
 */
@Handler(supports= SqlVisitDataDefinition.class)
public class SqlVisitDataEvaluator implements VisitDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Override
	public EvaluatedVisitData evaluate(VisitDataDefinition def, EvaluationContext context) throws EvaluationException {
		SqlVisitDataDefinition definition = (SqlVisitDataDefinition) def;
		EvaluatedVisitData data = new EvaluatedVisitData(definition, context);

		VisitIdSet visitIds = new VisitIdSet(VisitDataUtil.getVisitIdsForContext(context, false));
		if (visitIds.getSize() == 0) {
			return data;
		}

		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append(definition.getSql());
		for (Parameter p : definition.getParameters()) {
			q.addParameter(p.getName(), context.getParameterValue(p.getName()));
		}
		q.addParameter("visitIds", visitIds);

		Map<Integer, Object> results = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		data.setData(results);

		return data;
	}

}
