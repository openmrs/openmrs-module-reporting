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