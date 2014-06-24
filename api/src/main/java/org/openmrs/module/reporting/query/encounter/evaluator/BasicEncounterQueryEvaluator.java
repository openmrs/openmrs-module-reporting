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

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = BasicEncounterQuery.class)
public class BasicEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Autowired
	EvaluationService evaluationService;

    @Override
    public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());

        BasicEncounterQuery query = (BasicEncounterQuery) definition;
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
		q.whereIn("e.form", query.getForms());
		q.whereGreaterOrEqualTo("e.encounterDatetime", query.getOnOrAfter());
		q.whereLessOrEqualTo("e.encounterDatetime", query.getOnOrBefore());
		q.whereIn("e.location", query.getLocationList());
		q.whereEncounterIn("e.encounterId", context);

		if (query.getWhich() == null || query.getWhich() == TimeQualifier.ANY) {
			List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
			result.getMemberIds().addAll(results);
		}
		else {
			q.innerJoin("e.patient", "p");
			q.select("p.patientId");
			if (query.getWhich() == TimeQualifier.LAST) {
				q.orderDesc("e.encounterDatetime");
			}
			else {
				q.orderAsc("e.encounterDatetime");
			}

			ListMap<Integer, Integer> foundEncountersForPatients = new ListMap<Integer, Integer>();
			int maxNumPerPatient = ObjectUtil.nvl(query.getWhichNumber(), 1);
			for (Object[] row : evaluationService.evaluateToList(q, context)) {
				Integer encounterId = (Integer)row[0];
				Integer patientId = (Integer)row[1];
				foundEncountersForPatients.putInList(patientId, encounterId);
				if (foundEncountersForPatients.get(patientId).size() <= maxNumPerPatient) {
					result.add(encounterId);
				}
			}
		}

        return result;
    }
}
