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

        Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class);

		if (!convertedIds.keySet().isEmpty()) {
			// Create a new (patient) evaluation context using the retrieved ids
			EvaluationContext patientEvaluationContext = new EvaluationContext();
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
