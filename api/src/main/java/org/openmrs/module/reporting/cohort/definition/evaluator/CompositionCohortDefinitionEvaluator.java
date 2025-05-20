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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.evaluator.CompositionQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Evaluates an CompositionCohortDefinition and produces a Cohort
 */
@Handler(supports={CompositionCohortDefinition.class})
public class CompositionCohortDefinitionEvaluator extends CompositionQueryEvaluator<CohortDefinition, Patient> implements CohortDefinitionEvaluator {

	@Autowired
	CohortDefinitionService cohortDefinitionService;
	
	/**
	 * Default Constructor
	 */
	public CompositionCohortDefinitionEvaluator() {}

	@Override
	protected IdSet<Patient> evaluateQuery(Mapped<CohortDefinition> query, EvaluationContext context) throws EvaluationException {
		return cohortDefinitionService.evaluate(query, context);
	}

	@Override
	protected CohortDefinition getAllIdQuery() {
		return new AllPatientsCohortDefinition();
	}

	/**
     * @throws EvaluationException 
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		Set<Integer> ids = evaluateToIdSet(cohortDefinition, context).getMemberIds();
		return new EvaluatedCohort(new Cohort(ids), cohortDefinition, context);
    }
}