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
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.PatientEncounterQuery;

import java.util.Set;

/**
 * The logic that evaluates a {@link PatientEncounterQuery} and produces an {@link Query}
 */
@Handler(supports=PatientEncounterQuery.class)
public class PatientEncounterQueryEvaluator implements EncounterQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public PatientEncounterQueryEvaluator() { }
	
	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should return all of the encounter ids for all patients in the defined patient query
	 * @should filter results by patient and encounter given an EncounterEvaluationContext
	 * @should filter results by patient given an EvaluationContext
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		PatientEncounterQuery query = (PatientEncounterQuery) definition;
		EncounterQueryResult queryResult = new EncounterQueryResult(query, context);

		// Calculate the patients for this query
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(query.getPatientQuery(), context);

		// Get all of the encounters for all of these patients
		EvaluationContext ec = new EvaluationContext();
		ec.setBaseCohort(c);
		Set<Integer> ret = EncounterDataUtil.getEncounterIdsForContext(ec, false);

		// Limit that to only the passed in encounters if relevant
		if (context instanceof EncounterEvaluationContext) {
			EncounterEvaluationContext eec = (EncounterEvaluationContext) context;
			if (eec.getBaseEncounters() != null) {
				ret.retainAll(eec.getBaseEncounters().getMemberIds());
			}
		}

		queryResult.setMemberIds(ret);
		return queryResult;
	}
}
