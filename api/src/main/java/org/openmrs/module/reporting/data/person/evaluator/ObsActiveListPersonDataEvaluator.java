/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.evaluator;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObsActiveList;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsActiveListPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates an ObsActiveListPersonDataDefinition to produce a PersonData
 */
@Handler(supports = ObsActiveListPersonDataDefinition.class, order = 50)
public class ObsActiveListPersonDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		ObsActiveListPersonDataDefinition def = (ObsActiveListPersonDataDefinition) definition;
		EvaluatedPersonData evaluatedPersonData = new EvaluatedPersonData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return evaluatedPersonData;
		}
		
		if (def.getStartingConcepts() == null || def.getStartingConcepts().size() == 0) {
			return evaluatedPersonData;
		}
		
		// Retrieve all Observations for each patient, for all added and removed Concepts
		List<Object[]> startingObs = getObs(def.getStartingConcepts(), context);
		List<Object[]> endingObs = getObs(def.getEndingConcepts(), context);
		
		for (Object[] row : startingObs) {
			Integer pId = (Integer)row[0];
			Obs obs = (Obs)row[1];
			ObsActiveList l = (ObsActiveList)evaluatedPersonData.getData().get(pId);
			if (l == null) {
				l = new ObsActiveList(pId);
				evaluatedPersonData.addData(pId, l);
			}
			l.addStartingObs(obs);
		}

		for (Object[] row : endingObs) {
			Integer pId = (Integer)row[0];
			Obs obs = (Obs)row[1];
			ObsActiveList l = (ObsActiveList)evaluatedPersonData.getData().get(pId);
			if (l == null) {
				l = new ObsActiveList(pId);
				evaluatedPersonData.addData(pId, l);
			}
			l.addEndingObs(obs);
		}
		
		return evaluatedPersonData;
	}

	public List<Object[]> getObs(List<Concept> concepts, EvaluationContext context) {
		if (concepts == null || concepts.isEmpty()) {
			return new ArrayList<Object[]>();
		}
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.personId", "o");
		q.from(Obs.class, "o");
		q.wherePersonIn("o.personId", context);
		q.whereIn("o.concept", concepts);
		return evaluationService.evaluateToList(q, context);
	}
}
