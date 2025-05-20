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

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
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
		q.whereIn("pi.location", picd.getLocationsToMatch());
		
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
}