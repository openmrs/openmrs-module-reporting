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
		
		List<Object[]> queryResult = evaluationService.evaluateToList(q);
		
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
