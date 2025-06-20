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
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;

/**
 * Evaluates an InverseCohortDefinition and produces a Cohort
 */
@Handler(supports={InverseCohortDefinition.class})
public class InverseCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public InverseCohortDefinitionEvaluator() {}
	
	/**
     * @throws EvaluationException 
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return all patients who are not in the inner cohort definition
     * @should successfully use the context base cohort
     */
    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
    	InverseCohortDefinition icd = (InverseCohortDefinition) cohortDefinition;
    	if (icd.getBaseDefinition() == null) {
    		throw new MissingDependencyException("baseDefinition");
    	}
    	context = ObjectUtil.nvl(context, new EvaluationContext());
    	Cohort baseCohort;
    	try {
    		baseCohort = Context.getService(CohortDefinitionService.class).evaluate(icd.getBaseDefinition(), context);
    	} catch (Exception ex) {
    		throw new EvaluationException("base cohort", ex);
    	}
        Cohort allPatients = context.getBaseCohort() != null ? context.getBaseCohort() : Cohorts.allPatients(context);
		Cohort c = CohortUtil.subtract(allPatients, baseCohort);
		return new EvaluatedCohort(c, cohortDefinition, context);
    }
}