/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ObsInEncounterCohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates an DateOfPatientDataCohortDefinition and produces a Cohort
 */
@Handler(supports={ObsInEncounterCohortDefinition.class})
public class ObsInEncounterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		EvaluatedCohort ret = new EvaluatedCohort(cohortDefinition, context);
		ObsInEncounterCohortDefinition cd = (ObsInEncounterCohortDefinition) cohortDefinition;

		// Hibernate doesn't really support limit the way we need, so using this approach of 2 queries...

		// First we determine which encounters qualify

		HqlQueryBuilder encQuery = new HqlQueryBuilder();
		encQuery.select("e.patient.patientId, e.encounterId");
		encQuery.from(Encounter.class, "e");
		encQuery.whereIn("e.encounterType", cd.getEncounterTypes());
		encQuery.whereIn("e.location", cd.getEncounterLocations());
		encQuery.whereGreaterOrEqualTo("e.encounterDatetime", cd.getEncounterOnOrAfter());
		encQuery.whereLessOrEqualTo("e.encounterDatetime", cd.getEncounterOnOrBefore());
		encQuery.whereEncounterIn("e.encounterId", context);

		if (cd.getWhichEncounter() == TimeQualifier.LAST) {
			encQuery.orderAsc("e.encounterDatetime").orderAsc("e.dateCreated"); // Ascending since we take last value found
		}
		else if (cd.getWhichEncounter() == TimeQualifier.FIRST) {
			encQuery.orderDesc("e.encounterDatetime").orderDesc("e.dateCreated"); // Descending since we take last value found
		}
		Set<Integer> encountersToInclude = new HashSet<Integer>();
		if (cd.getWhichEncounter() == TimeQualifier.LAST || cd.getWhichEncounter() == TimeQualifier.FIRST) {
			Map<Integer, Integer> encountersByPatient = evaluationService.evaluateToMap(encQuery, Integer.class, Integer.class, context);
			encountersToInclude.addAll(encountersByPatient.values());
		}
		else {
            List<Object[]> rawResults = evaluationService.evaluateToList(encQuery, context);
            for (Object[] resultRow : rawResults) {
                encountersToInclude.add((Integer)resultRow[1]);
            }
		}

		// Now we can determine if there are any matching obs in those encounters that meet our criteria

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.personId");
		q.from(Obs.class, "o");
		q.whereEqual("o.concept", cd.getQuestion());
		q.where("o.valueDatetime", cd.getValueOperator1(), cd.getValueDatetime1());
		q.where("o.valueDatetime", cd.getValueOperator2(), cd.getValueDatetime2());
		q.whereIdIn("o.encounter.encounterId", encountersToInclude);

		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		ret.getMemberIds().addAll(pIds);

		return ret;
    }
}