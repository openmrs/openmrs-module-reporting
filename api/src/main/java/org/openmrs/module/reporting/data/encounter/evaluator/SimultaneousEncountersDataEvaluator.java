/*
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
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SimultaneousEncountersDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports= SimultaneousEncountersDataDefinition.class, order=50)
public class SimultaneousEncountersDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedEncounterData results = new EvaluatedEncounterData(definition, context);
        SimultaneousEncountersDataDefinition def = (SimultaneousEncountersDataDefinition) definition;

		if (def.getEncounterTypeList() != null && def.getEncounterTypeList().isEmpty()) {
			return results;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("enc.id", "other");
		q.from(Encounter.class, "enc").from(Encounter.class, "other");
		q.where("enc.encounterDatetime = other.encounterDatetime");
		q.where("enc.patient.id = other.patient.id");
		q.where("enc.id != other.id");
		q.whereEqual("enc.voided", false);
		q.whereEqual("other.voided", false);
		q.whereIn("other.encounterType", def.getEncounterTypeList());
		q.whereEncounterIn("enc.id", context);
		q.orderAsc("other.dateCreated");  // use the most-recently-entered encounter

		Map<Integer, Object> data  = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		results.setData(data);

		return results;
    }

}
