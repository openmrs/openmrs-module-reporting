/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.obs.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.AllObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;

/**
 * The logic that evaluates a {@link AllObsQuery} and produces an {@link ObsQueryResult}
 */
@Handler(supports= AllObsQuery.class)
public class AllObsQueryEvaluator implements ObsQueryEvaluator {

	/**
	 * @see ObsQueryEvaluator#evaluate(ObsQuery, EvaluationContext)
	 * @should return all of the obs ids for all patients in the defined query
	 * @should filter results by patient and obs given an ObsEvaluationContext
	 * @should filter results by patient given an EvaluationContext
	 */
    @Override
    public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		ObsQueryResult queryResult = new ObsQueryResult(definition, context);
		queryResult.setMemberIds(ObsDataUtil.getObsIdsForContext(context, false));
		return queryResult;
    }

}
