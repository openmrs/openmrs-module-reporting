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
package org.openmrs.module.reporting.data.visit.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.ObsForVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates an ObsForVisitDataDefinition to produce a VisitData that contains the observations recorded for a visit, based on a provided concept
 */
@Handler(supports=ObsForVisitDataDefinition.class, order=50)
public class ObsForVisitDataEvaluator implements VisitDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Autowired
	PatientDataService patientDataService;

	@Autowired
	VisitDataService visitDataService;

	/** 
	 * @see VisitDataEvaluator#evaluate(VisitDataDefinition, EvaluationContext)
	 * @should return the obs that matches the passed definition configuration
	 */
	public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {

		ObsForVisitDataDefinition visitDef = (ObsForVisitDataDefinition) definition;

		EvaluatedVisitData evaluatedData = new EvaluatedVisitData(visitDef, context);
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return evaluatedData;
		}


		Concept concept = visitDef.getQuestion();
		List<Concept> childConcepts = getChildConcepts(concept);

		HqlQueryBuilder q = new HqlQueryBuilder();

		q.select("v.visitId", "o");
		q.from(Obs.class, "o");
		q.innerJoin("o.encounter", "e");
		q.innerJoin("e.visit", "v");
		q.whereIn("o.concept", childConcepts);
		q.whereVisitIn("v.visitId", context);

		if (visitDef.getWhich() == TimeQualifier.LAST) {
			q.orderDesc("o.obsDatetime");
		}
		else {
			q.orderAsc("o.obsDatetime");
		}

		List<Object[]> queryResult = evaluationService.evaluateToList(q, context);

		
		ListMap<Integer, Obs> obsForVisits = new ListMap<Integer, Obs>();
		for (Object[] row : queryResult) {
			obsForVisits.putInList((Integer)row[0], (Obs)row[1]);
		}

		for (Integer vid : obsForVisits.keySet()) {
			List<Obs> l = obsForVisits.get(vid);
			if (visitDef.getWhich() == TimeQualifier.LAST || visitDef.getWhich() == TimeQualifier.FIRST) {
				evaluatedData.addData(vid, l.get(0));
			} else {
			evaluatedData.addData(vid, l);
			}
		}
		return evaluatedData;
	}

	/**
	 * @param concept a concept of which one want to get set members, if applicable
	 * @return the concepts members if the provided parameter is indeed a concept set, otherwise returns the input parameter concept
	 * 
	 */
	private List<Concept> getChildConcepts (Concept concept) {
		List<Concept> childConcepts = new ArrayList<Concept>();
		if (concept.getSetMembers() == null || concept.getSetMembers().isEmpty()) {
			childConcepts.add(concept);
		} else {
			for (Concept c : concept.getSetMembers()) {
				// concept is a set, call the function again
				childConcepts.addAll(getChildConcepts(c));
			}	
		}
		return childConcepts;
	}
}