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
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a EncounterDatetimeDataDefinition to produce a EncounterData
 */
public abstract class EncounterPropertyDataEvaluator implements EncounterDataEvaluator {

	@Autowired
	private EvaluationService evaluationService;

	public abstract String getPropertyName();

	/**
	 * @see org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator#evaluate(org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return all encounter datetimes given the passed context
	 */
	public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.encounterId", "e."+getPropertyName());
		q.from(Encounter.class, "e");
		q.whereEncounterIn("e.encounterId", context);
		Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		c.setData(data);
		return c;
	}
}
