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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

/**
 * The logic that evaluates a {@link AllEncounterQuery} and produces an {@link Query}
 */
@Handler(supports=AllEncounterQuery.class)
public class AllEncounterQueryEvaluator implements EncounterQueryEvaluator {

	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should return all of the encounter ids for all patients in the defined query
	 * @should filter results by patient and encounter given an EncounterEvaluationContext
	 * @should filter results by patient given an EvaluationContext
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);
		queryResult.setMemberIds(EncounterDataUtil.getEncounterIdsForContext(context, false));
		return queryResult;
	}
}
