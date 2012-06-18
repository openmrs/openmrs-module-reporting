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
package org.openmrs.module.reporting.data.encounter;

import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;

/**
 * Encounter Data Utility methods
 */
public class EncounterDataUtil {

	/**
	 * @return the base set of encounter ids relevant for the passed EvaluationContext or null for all encounter ids
	 * If returnNullForAllEncounterIds is false, then this will return all encounter ids in the system if unconstrained by the context
	 */
	public static Set<Integer> getEncounterIdsForContext(EvaluationContext context, boolean returnNullForAllEncounterIds) throws EvaluationException {
		boolean isConstrained = context.getBaseCohort() != null;
		if (context instanceof EncounterEvaluationContext) {
			isConstrained = isConstrained || ((EncounterEvaluationContext)context).getBaseEncounters() != null;
		}
		if (!returnNullForAllEncounterIds || isConstrained) {
			EncounterQueryResult allIds = Context.getService(EncounterQueryService.class).evaluate(new AllEncounterQuery(), context);
			return allIds.getMemberIds();
		}
		return null;
	}
}
