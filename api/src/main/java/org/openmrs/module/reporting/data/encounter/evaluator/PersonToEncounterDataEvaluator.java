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
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;

/**
 * Evaluates a PersonToEncounterDataDefinition to produce a EncounterData
 */
@Handler(supports=PersonToEncounterDataDefinition.class, order=50)
public class PersonToEncounterDataEvaluator implements EncounterDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see EncounterDataEvaluator#evaluate(EncounterDataDefinition, EvaluationContext)
	 * @should return person data by for each encounter in the passed context
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
			// create a new (person) evaluation context using the retrieved ids
			PersonEvaluationContext personEvaluationContext = new PersonEvaluationContext(context, null);
			personEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));
			personEvaluationContext.setBasePersons(new PersonIdSet(new HashSet<Integer>(convertedIds.values())));

			// evaluate the joined definition via this person context
			PersonToEncounterDataDefinition def = (PersonToEncounterDataDefinition) definition;
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(def.getJoinedDefinition(), personEvaluationContext);

			// now create the result set by mapping the results in the person data set to encounter ids
			for (Integer encId : convertedIds.keySet()) {
				c.addData(encId, pd.getData().get(convertedIds.get(encId)));
			}
		}

        return c;

	}
}
