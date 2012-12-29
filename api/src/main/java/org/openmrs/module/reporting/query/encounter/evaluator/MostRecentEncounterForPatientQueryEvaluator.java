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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MostRecentEncounterForPatientQuery;

/**
 * The logic that evaluates a {@link MostRecentEncounterForPatientQueryEvaluator} and produces an {@link EncounterQueryResult}
 */
@Handler(supports=MostRecentEncounterForPatientQuery.class)
public class MostRecentEncounterForPatientQueryEvaluator implements EncounterQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public MostRecentEncounterForPatientQueryEvaluator() { }
	
	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should include the encounter at the start of the specified onOrAfter date
	 * @should include the encounter at the end of the specified onOrAfter date
	 * @should include the encounter at the start of the specified onOrBefore date
	 * @should include the encounter at the end of the specified onOrBefore date
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		MostRecentEncounterForPatientQuery query = (MostRecentEncounterForPatientQuery) definition;
		EncounterQueryResult queryResult = new EncounterQueryResult(query, context);
		
		// TODO: Move this into a service and find a way to make it more efficient
		StringBuilder q = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		
		q.append("select 	encounterId, patientId ");
		q.append("from 		Encounter ");
		q.append("where 	voided = false ");
		if (query.getEncounterTypes() != null) {
			q.append("and 		encounterType in (:encounterTypes) ");
			params.put("encounterTypes", query.getEncounterTypes());
		}
		if (query.getOnOrAfter() != null) {
			q.append("and encounterDatetime >= :onOrAfter ");
			params.put("onOrAfter", query.getOnOrAfter());
		}
		if (query.getOnOrBefore() != null) {
			q.append("and encounterDatetime <= :onOrBefore ");
			params.put("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(query.getOnOrBefore()));
		}
		if (context.getBaseCohort() != null) {
			q.append(" and patientId in (:patientIds) ");
			params.put("patientIds", context.getBaseCohort().getMemberIds());
		}
		if (context instanceof EncounterEvaluationContext) {
			EncounterEvaluationContext eec = (EncounterEvaluationContext) context;
			if (eec.getBaseEncounters() != null) {
				q.append(" and encounterId in (:encounterIds) ");
				params.put("encounterIds", eec.getBaseEncounters().getMemberIds());
			}
		}
		q.append("order by encounterDatetime asc ");
		if (context.getLimit() != null) {
			q.append(" limit " + context.getLimit());
		}
		
		List<Object> ret = Context.getService(DataSetQueryService.class).executeHqlQuery(q.toString(), params);
		Map<Integer, Integer> pIdToEncId = new HashMap<Integer, Integer>();
		for (Object o : ret) {
			Object[] row = (Object[]) o;
			pIdToEncId.put((Integer)row[1], (Integer)row[0]);
		}
		queryResult.setMemberIds(new HashSet<Integer>(pIdToEncId.values()));
		return queryResult;
	}
}
