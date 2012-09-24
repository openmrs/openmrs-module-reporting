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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObsActiveList;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsActiveListPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an ObsActiveListPersonDataDefinition to produce a PersonData
 */
@Handler(supports = ObsActiveListPersonDataDefinition.class, order = 50)
public class ObsActiveListPersonDataEvaluator implements PersonDataEvaluator {
	
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
		
		DataSetQueryService queryService = Context.getService(DataSetQueryService.class);
		
		// Retrieve all Observations for each patient, for all added and removed Concepts
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from Obs ");
		hql.append("where voided = false ");
		hql.append("and concept in (:concepts) ");

		if (context.getBaseCohort() != null) {
			hql.append("and personId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		List<Object> startingObs = new ArrayList<Object>();
		List<Object> endingObs = new ArrayList<Object>();
		
		m.put("concepts", def.getStartingConcepts());
		startingObs = queryService.executeHqlQuery(hql.toString(), m);
		
		if (def.getEndingConcepts() != null && !def.getEndingConcepts().isEmpty()) {
			m.put("concepts", def.getEndingConcepts());
			endingObs = queryService.executeHqlQuery(hql.toString(), m);
		}
		
		for (Object o : startingObs) {
			Obs obs = (Obs)o;
			ObsActiveList l = (ObsActiveList)evaluatedPersonData.getData().get(obs.getPersonId());
			if (l == null) {
				l = new ObsActiveList(obs.getPersonId());
				evaluatedPersonData.addData(obs.getPersonId(), l);
			}
			l.addStartingObs(obs);
		}
		
		for (Object o : endingObs) {
			Obs obs = (Obs)o;
			ObsActiveList l = (ObsActiveList)evaluatedPersonData.getData().get(obs.getPersonId());
			if (l == null) {
				l = new ObsActiveList(obs.getPersonId());
				evaluatedPersonData.addData(obs.getPersonId(), l);
			}
			l.addEndingObs(obs);
		}
		
		return evaluatedPersonData;
	}
}
