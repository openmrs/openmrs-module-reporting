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

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

/**
 * Evaluates an PatientCharacteristicCohortDefinition and produces a Cohort
 */
@Handler(supports={AllPatientsCohortDefinition.class})
public class AllPatientsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    EvaluationService evaluationService;

	/**
	 * Default Constructor
	 */
	public AllPatientsCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return all non-voided patients, optionally limited to those in the passed context
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	EvaluatedCohort c = new EvaluatedCohort(context.getBaseCohort(), cohortDefinition, context);
    	if (context.getBaseCohort() == null) {
            // Everywhere else in the reporting module we call Cohorts.allPatients(EvaluationContext) to get the list
            // of all patients, but in this one place we intentionally go directly to the database. This avoids infinite
            // recursion, since the Cohorts.allPatients method delegates to this evaluator.
            //
            // As long as API consumers don't directly call this evaluator, but go through CohortDefinitionService,
            // they will correctly have test patients excluded.

            HqlQueryBuilder query = new HqlQueryBuilder().select("p.patientId").from(Patient.class, "p");
            List<Integer> ptIds = evaluationService.evaluateToList(query, Integer.class, context);

            c.setMemberIds(new HashSet<Integer>(ptIds));
        }
    	return c;
    }
}