/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
