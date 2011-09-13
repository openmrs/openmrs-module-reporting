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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.util.OpenmrsUtil;

/**
 * The logic that evaluates a {@link AllEncounterQuery} and produces an {@link Query}
 */
@Handler(supports=AllEncounterQuery.class)
public class AllEncounterQueryEvaluator implements EncounterQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public AllEncounterQueryEvaluator() { }
	
	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should return all of the encounter ids for all patients in the defined query
	 * @should filter results by patient and encounter given an EncounterEvaluationContext
	 * @should filter results by patient given an EvaluationContext
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		AllEncounterQuery query = (AllEncounterQuery) definition;
		EncounterQueryResult queryResult = new EncounterQueryResult(query, context);
		
		// TODO: Move this into a service and find a way to make it more efficient
		StringBuilder sqlQuery = new StringBuilder("select encounter_id from encounter where voided = false");
		if (context.getBaseCohort() != null) {
			sqlQuery.append(" and patient_id in (" + context.getBaseCohort().getCommaSeparatedPatientIds() + ")");
		}
		if (context instanceof EncounterEvaluationContext) {
			EncounterEvaluationContext eec = (EncounterEvaluationContext) context;
			sqlQuery.append(" and encounter_id in (" + OpenmrsUtil.join(eec.getBaseEncounters().getMemberIds(), ",") + ")");
		}
		if (context.getLimit() != null) {
			sqlQuery.append(" limit " + context.getLimit());
		}
		
		List<List<Object>> ret = Context.getAdministrationService().executeSQL(sqlQuery.toString(), true);
		for (List<Object> l : ret) {
			queryResult.add((Integer)l.get(0));
		}
		return queryResult;
	}
}
