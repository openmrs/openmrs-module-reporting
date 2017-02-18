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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ObsInEncounterCohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.DefinitionUtil;
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
		encQuery.whereIn("e.location", getEncounterLocations(cd));
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

    private List<Location> getEncounterLocations(ObsInEncounterCohortDefinition cd) {
    	if (cd.isIncludeChildLocations()) {
    		return DefinitionUtil.getAllLocationsAndChildLocations(cd.getEncounterLocations());
		}
		return cd.getEncounterLocations();
	}
}