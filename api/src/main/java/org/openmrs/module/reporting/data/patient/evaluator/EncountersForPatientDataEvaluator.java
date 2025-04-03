/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates an EncountersForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=EncountersForPatientDataDefinition.class, order=50)
public class EncountersForPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		EncountersForPatientDataDefinition def = (EncountersForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.patient.patientId", "e");
		q.from(Encounter.class, "e");
		q.wherePatientIn("e.patient.patientId", context);
		q.whereIn("e.encounterType", def.getTypes());
		q.whereIn("e.location", def.getLocationList());
		q.whereGreaterOrEqualTo("e.encounterDatetime", def.getOnOrAfter());
		q.whereLessOrEqualTo("e.encounterDatetime", def.getOnOrBefore());

        if (def.getOnlyInActiveVisit()) {
			q.whereNull("e.visit.stopDatetime");
        }

		if (def.getWhich() == TimeQualifier.LAST) {
			q.orderDesc("e.encounterDatetime");
		}
		else {
			q.orderAsc("e.encounterDatetime");
		}
		
		List<Object[]> queryResult = evaluationService.evaluateToList(q, context);
		
		ListMap<Integer, Encounter> encountersForPatients = new ListMap<Integer, Encounter>();
		for (Object[] row : queryResult) {
			encountersForPatients.putInList((Integer)row[0], (Encounter)row[1]);
		}
		
		for (Integer pId : encountersForPatients.keySet()) {
			List<Encounter> l = encountersForPatients.get(pId);
			if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
				c.addData(pId, l.get(0));
			}
			else {
				c.addData(pId, l);
			}
		}
		
		return c;
	}
}
