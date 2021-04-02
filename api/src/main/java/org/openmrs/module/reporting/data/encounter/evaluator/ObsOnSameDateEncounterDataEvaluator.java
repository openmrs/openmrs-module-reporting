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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsOnSameDateEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates a ObsOnSameDateEncounterDataDefinition to produce EncounterData
 */
@Handler(supports=ObsOnSameDateEncounterDataDefinition.class, order=40)
public class ObsOnSameDateEncounterDataEvaluator implements EncounterDataEvaluator {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
	EncounterDataService encounterDataService;

    @Autowired
	EvaluationService evaluationService;
    
    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        ObsOnSameDateEncounterDataDefinition def = (ObsOnSameDateEncounterDataDefinition) definition;
        EvaluatedEncounterData data = new EvaluatedEncounterData();

        // First get a Map from encounterId to patient:date

		HqlQueryBuilder encQuery = new HqlQueryBuilder();
		encQuery.select("encounter.encounterId, encounter.patient.patientId, encounter.encounterDatetime");
		encQuery.from(Encounter.class, "encounter");
		encQuery.whereEncounterIn("encounter.encounterId", context);
		List<Object[]> encounterData = evaluationService.evaluateToList(encQuery, context);

		Map<Integer, String> encounterToDateMap = new HashMap<Integer, String>();
		Set<Integer> pIds = new HashSet<Integer>();
		for (Object[] enc : encounterData) {
			Integer encId = (Integer)enc[0];
			Integer pId = (Integer)enc[1];
			String encDate = DateUtil.formatDate((Date)enc[2], "yyyyMMdd");
			encounterToDateMap.put(encId, pId + ":" + encDate);
			pIds.add(pId);
		}

		// Next get a Map from patient:date to List<Obs>

		HqlQueryBuilder obsQuery = new HqlQueryBuilder();
		obsQuery.select("obs.encounter.patient.patientId, obs.encounter.encounterDatetime, obs");
		obsQuery.from(Obs.class, "obs");
		obsQuery.whereEqual("obs.concept", def.getQuestion());
		obsQuery.whereIn("obs.valueCoded", def.getAnswers());
		obsQuery.whereIdIn("obs.encounter.patient.patientId", pIds);
		obsQuery.orderDesc("obs.obsDatetime");
		List<Object[]> obsData = evaluationService.evaluateToList(obsQuery, context);

		Map<String, List<Obs>> dateToObsMap = new HashMap<String, List<Obs>>();
		for (Object[] obs : obsData) {
			Integer pId = (Integer)obs[0];
			String encDate = DateUtil.formatDate((Date)obs[1], "yyyyMMdd");
			String dateKey = pId + ":" + encDate;
			List<Obs> l = dateToObsMap.get(dateKey);
			if (l == null) {
				l = new ArrayList<Obs>();
				dateToObsMap.put(dateKey, l);
			}
			l.add((Obs)obs[2]);
		}

		// Finally, join those together to get a Map from encounterId to List<Obs>

		for (Integer encId : encounterToDateMap.keySet()) {
			String dateKey = encounterToDateMap.get(encId);
			List<Obs> obsList = dateToObsMap.get(dateKey);

			if (def.isSingleObs() && (obsList != null)) {
				// return only the most recent Obs
				data.addData(encId, obsList.get(0));
			} else {
				data.addData(encId, obsList);
			}
		}

        return data;
    }
}
