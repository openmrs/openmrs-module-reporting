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
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PatientToEncounterDataDefinition to produce a EncounterData
 */
@Handler(supports=PatientToEncounterDataDefinition.class, order=50)
public class PatientToEncounterDataEvaluator implements EncounterDataEvaluator {

	/** 
	 * @see EncounterDataEvaluator#evaluate(EncounterDataDefinition, EvaluationContext)
	 * @should return person data by for each patient in the passed cohort
	 */
	public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);
		PatientToEncounterDataDefinition def = (PatientToEncounterDataDefinition)definition;
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def.getJoinedDefinition(), context);
		DataSetQueryService dqs = Context.getService(DataSetQueryService.class);
		Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, true);
		Map<Integer, Integer> convertedIds = dqs.convertData(Patient.class, "patientId", pd.getData().keySet(), Encounter.class, "patient.patientId", encIds);
		for (Integer encId : convertedIds.keySet()) {
			c.addData(encId, pd.getData().get(convertedIds.get(encId)));
		}
		return c;
	}
}
