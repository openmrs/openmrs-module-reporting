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

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PatientToEncounterDataDefinition to produce a EncounterData
 */
@Handler(supports=PatientToEncounterDataDefinition.class, order=50)
public class PatientToEncounterDataEvaluator implements EncounterDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see EncounterDataEvaluator#evaluate(EncounterDataDefinition, EvaluationContext)
	 * @should return patient data for each encounter in the passed cohort
	 */
    @Override
	public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

		// create a map of encounter ids -> patient ids

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.encounterId", "e.patient.patientId");
		q.from(Encounter.class, "e");
		q.whereEncounterIn("e.encounterId", context);

        Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);

		if (!convertedIds.keySet().isEmpty()) {
			// Create a new (patient) evaluation context using the retrieved ids
			EvaluationContext patientEvaluationContext = context.shallowCopy();
			patientEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));

			// evaluate the joined definition via this patient context
			PatientToEncounterDataDefinition def = (PatientToEncounterDataDefinition) definition;
			EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def.getJoinedDefinition(), patientEvaluationContext);

			// now create the result set by mapping the results in the patient data set to encounter ids
			for (Integer encId : convertedIds.keySet()) {
				c.addData(encId, pd.getData().get(convertedIds.get(encId)));
			}
		}

		return c;
	}
}
