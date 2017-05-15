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

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates a PatientIdentifierCohortDefinition and produces a Cohort
 */
@Handler(supports={PatientIdentifierCohortDefinition.class})
public class PatientIdentifierCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/**
	 * Default Constructor
	 */
	public PatientIdentifierCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return patients who have identifiers of the passed types
     * @should return patients who have identifiers matching the passed locations
     * @should return patients who have identifiers matching the passed text
     * @should return patients who have identifiers matching the passed regular expression
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {

		PatientIdentifierCohortDefinition picd = (PatientIdentifierCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EvaluatedCohort ret = new EvaluatedCohort(null, picd, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pi.patient.patientId");
		if (ObjectUtil.notNull(picd.getRegexToMatch())) {
			q.select("pi.identifier");
		}
		q.from(PatientIdentifier.class, "pi");
		q.whereIn("pi.identifierType", picd.getTypesToMatch());
		q.whereIn("pi.location", getLocationsToMatch(picd));
		
		if (ObjectUtil.notNull(picd.getTextToMatch())) {
			if (picd.getTextToMatch().contains("%")) {
				q.whereLike("pi.identifier", picd.getTextToMatch());
			}
			else {
				q.whereEqual("pi.identifier", picd.getTextToMatch());
			}
		}

		q.wherePatientIn("pi.patient.patientId", context);

		List<Object[]> results = evaluationService.evaluateToList(q, context);

		for (Object[] row : results) {
			boolean include = true;
			if (ObjectUtil.notNull(picd.getRegexToMatch())) {
				include = (row.length == 2 && row[1] != null && row[1].toString().matches(picd.getRegexToMatch()));
			}
			if (include) {
				ret.addMember((Integer)row[0]);
			}
		}
		
		return ret;
    }

    private List<Location> getLocationsToMatch(PatientIdentifierCohortDefinition cd) {
    	if (cd.isIncludeChildLocations()) {
    		return DefinitionUtil.getAllLocationsAndChildLocations(cd.getLocationsToMatch());
		}
		return cd.getLocationsToMatch();
	}
}