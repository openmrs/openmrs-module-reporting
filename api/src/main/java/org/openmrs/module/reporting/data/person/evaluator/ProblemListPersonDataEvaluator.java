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
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ProblemListPersonDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an ProblemListPersonData to produce a PersonData
 */
@Handler(supports = ProblemListPersonDataDefinition.class, order = 50)
public class ProblemListPersonDataEvaluator implements PersonDataEvaluator {
	
	/**
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	                                                                                               throws EvaluationException {
		
		ProblemListPersonDataDefinition def = (ProblemListPersonDataDefinition) definition;
		EvaluatedPersonData evaluatedPersonData = new EvaluatedPersonData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return evaluatedPersonData;
		}
		
		if (def.getProblemAddedConcepts() == null || def.getProblemAddedConcepts().size() == 0) {
			return evaluatedPersonData;
		}
		
		DataSetQueryService queryService = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from Obs ");
		hql.append("where voided = false ");
		
		if (context.getBaseCohort() != null) {
			hql.append("and personId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		//Add all Obs to the Set whose question is in the problemAddedConcepts List
		StringBuilder filter = new StringBuilder();
		filter.append("and concept in (:addedConcepts) ");
		m.put("addedConcepts", def.getProblemAddedConcepts());
		
		List<Object> problemsAdded = queryService.executeHqlQuery(new StringBuilder().append(hql).append(filter).toString(),
		    m);
		
		//Remove any Obs from the Set whose valueCoded matches the valueCoded of any Obs 
		//in the problemRemovedConcepts List and the obsDatetime of the Concept in the List
		//is before the obsDatetime of the problemRemoved obsDatetime.
		List<Object> problemsResolved = new ArrayList<Object>();
		if (def.getProblemRemovedConcepts() != null && def.getProblemRemovedConcepts().size() > 0) {
			filter = new StringBuilder();
			filter.append("and concept not in (:removedConcepts) ");
			m.put("removedConcepts", def.getProblemRemovedConcepts());
			m.remove("addedConcepts");
			problemsResolved = queryService.executeHqlQuery(new StringBuilder().append(hql).append(filter).toString(), m);
		}
		
		ListMap<Integer, Obs> obsForPatients = new ListMap<Integer, Obs>();
		for (Object o : problemsAdded) {
			Obs obs = (Obs) o;
			if (!isProblemResolved(obs, problemsResolved)) {
				obsForPatients.putInList(obs.getPersonId(), obs);
			}
		}
		
		for (Integer pId : obsForPatients.keySet()) {
			evaluatedPersonData.addData(pId, obsForPatients.get(pId));
		}
		
		return evaluatedPersonData;
	}
	
	/**
	 * Check if a given observation is resolved
	 * 
	 * @param obs the observation
	 * @param problemsResolved a list of problem resolved observations
	 * @return true if resolved, else false
	 */
	private boolean isProblemResolved(Obs obs, List<Object> problemsResolved) {
		for (Object o : problemsResolved) {
			Obs resolvedObs = (Obs) o;
			
			if (obs.getValueCoded() == null || resolvedObs.getValueCoded() == null) {
				continue; //Can problem lists have a null value coded???
			}
			
			if (!obs.getPerson().equals(resolvedObs.getPerson())) {
				continue; //not same person
			}
			
			//Check if valueCoded matches the valueCoded of the problems resolved obs 
			if (!obs.getValueCoded().equals(resolvedObs.getValueCoded())) {
				continue;
			}
			
			//Check if obsDatetime is before that of the problem resolved obs
			if (obs.getObsDatetime().before(resolvedObs.getObsDatetime())) {
				return true;
			}
		}
		
		return false;
	}
}
