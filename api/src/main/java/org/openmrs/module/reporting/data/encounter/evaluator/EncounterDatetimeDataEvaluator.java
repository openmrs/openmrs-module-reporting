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

import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a EncounterDatetimeDataDefinition to produce a EncounterData
 */
@Handler(supports=EncounterDatetimeDataDefinition.class, order=50)
public class EncounterDatetimeDataEvaluator implements EncounterDataEvaluator {

	/** 
	 * @see EncounterDataEvaluator#evaluate(EncounterDataDefinition, EvaluationContext)
	 * @should return all encounter datetimes given the passed context
	 */
	public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);		
		Map<Integer, Object> data = qs.getPropertyValues(Encounter.class, "encounterDatetime", context);
		c.setData(data);
		return c;
	}
}
