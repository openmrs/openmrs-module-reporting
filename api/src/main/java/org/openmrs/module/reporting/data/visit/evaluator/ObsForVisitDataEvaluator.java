/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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