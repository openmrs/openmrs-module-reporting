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
