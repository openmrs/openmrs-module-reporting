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
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

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
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return all patients who are not in the inner cohort definition
     * @should successfully use the context base cohort
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	InverseCohortDefinition icd = (InverseCohortDefinition) cohortDefinition;
    	context = ObjectUtil.nvl(context, new EvaluationContext());
    	Cohort allPatients = ObjectUtil.nvl(context.getBaseCohort(), Context.getPatientSetService().getAllPatients());
		Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(icd.getBaseDefinition(), context);
		return Cohort.subtract(allPatients, baseCohort);
    }
}