/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AuditEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = AuditEncounterQuery.class)
public class AuditEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Autowired
	EvaluationService evaluationService;

    @Override
    public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());

		AuditEncounterQuery query = (AuditEncounterQuery) definition;
        EncounterQueryResult result = new EncounterQueryResult(query, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return result;
		}
		if (context instanceof EncounterEvaluationContext) {
			EncounterIdSet baseEncounters = ((EncounterEvaluationContext) context).getBaseEncounters();
			if (baseEncounters != null && baseEncounters.getMemberIds().isEmpty()) {
				return result;
			}
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.encounterId");
		q.from(Encounter.class, "e");
		q.whereIn("e.encounterType", query.getEncounterTypes());
		q.whereGreaterOrEqualTo("e.dateCreated", query.getCreatedOnOrAfter());
		q.whereLessOrEqualTo("e.dateCreated", query.getCreatedOnOrBefore());
		q.whereEncounterIn("e.encounterId", context);
		q.orderDesc("e.dateCreated");
		q.limit(query.getLatestCreatedNumber());

		List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
		result.getMemberIds().addAll(results);
        return result;
    }
}
