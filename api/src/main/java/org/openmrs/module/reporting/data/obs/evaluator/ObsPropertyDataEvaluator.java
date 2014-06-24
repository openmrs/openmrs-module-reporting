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
package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Obs;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a property of Obs to produce a EncounterData
 */
public abstract class ObsPropertyDataEvaluator implements ObsDataEvaluator {

	@Autowired
	private EvaluationService evaluationService;

	public abstract String getPropertyName();

	/**
	 * @see org.openmrs.module.reporting.data.obs.evaluator.ObsDataEvaluator#evaluate(org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return all encounter datetimes given the passed context
	 */
	public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedObsData c = new EvaluatedObsData(definition, context);
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.obsId", "o."+getPropertyName());
		q.from(Obs.class, "o");
		q.whereObsIn("o.obsId", context);
		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(data);
		return c;
	}
}
