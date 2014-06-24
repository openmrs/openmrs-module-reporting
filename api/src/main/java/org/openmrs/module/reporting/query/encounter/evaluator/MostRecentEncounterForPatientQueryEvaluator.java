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
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MostRecentEncounterForPatientQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * The logic that evaluates a {@link MostRecentEncounterForPatientQueryEvaluator} and produces an {@link EncounterQueryResult}
 */
@Handler(supports=MostRecentEncounterForPatientQuery.class)
public class MostRecentEncounterForPatientQueryEvaluator implements EncounterQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * Public constructor
	 */
	public MostRecentEncounterForPatientQueryEvaluator() { }
	
	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should find an encounter on the onOrBefore date if passed in time is at midnight
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		MostRecentEncounterForPatientQuery query = (MostRecentEncounterForPatientQuery) definition;
		EncounterQueryResult queryResult = new EncounterQueryResult(query, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.patient.patientId", "e.encounterId");
		q.from(Encounter.class, "e");
		q.whereIn("e.encounterType", query.getEncounterTypes());
		q.whereGreaterOrEqualTo("e.encounterDatetime", query.getOnOrAfter());
		q.whereLessOrEqualTo("e.encounterDatetime", query.getOnOrBefore());
		q.whereEncounterIn("e.encounterId", context);
		q.orderAsc("e.encounterDatetime");

		Map<Integer, Integer> pIdToEncId = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);
		queryResult.getMemberIds().addAll(pIdToEncId.values());
		return queryResult;
	}
}
