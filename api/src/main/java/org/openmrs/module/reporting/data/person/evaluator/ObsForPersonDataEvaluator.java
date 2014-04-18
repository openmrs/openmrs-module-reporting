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

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates an ObsForPersonDataDefinition to produce a PersonData
 */
@Handler(supports=ObsForPersonDataDefinition.class, order=50)
public class ObsForPersonDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		ObsForPersonDataDefinition def = (ObsForPersonDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.personId", "o");
		q.from(Obs.class, "o");
		q.wherePersonIn("o.personId", context);
		q.whereEqual("o.concept", def.getQuestion());
		q.whereIn("o.encounter.encounterType", def.getEncounterTypeList());
		q.whereIn("o.encounter.form", def.getFormList());
		q.whereGreaterOrEqualTo("o.obsDatetime", def.getOnOrAfter());
		q.whereLessOrEqualTo("o.obsDatetime", def.getOnOrBefore());
		if (def.getWhich() == TimeQualifier.LAST) {
			q.orderDesc("o.obsDatetime");
		}
		else {
			q.orderAsc("o.obsDatetime");
		}

		List<Object[]> queryResult = evaluationService.evaluateToList(q);
		
		ListMap<Integer, Obs> obsForPatients = new ListMap<Integer, Obs>();
		for (Object[] row : queryResult) {
			obsForPatients.putInList((Integer)row[0], (Obs)row[1]);
		}
		
		for (Integer pId : obsForPatients.keySet()) {
			List<Obs> l = obsForPatients.get(pId);
			if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
				c.addData(pId, l.get(0));
			}
			else {
				c.addData(pId, l);
			}
		}
		
		return c;
	}
}
