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
