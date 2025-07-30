/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
