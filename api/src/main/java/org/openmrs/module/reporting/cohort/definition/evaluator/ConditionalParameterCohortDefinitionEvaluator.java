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
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ConditionalParameterCohortDefinition.class)
public class ConditionalParameterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		ConditionalParameterCohortDefinition cd = (ConditionalParameterCohortDefinition) cohortDefinition;
        Cohort ret = new Cohort();
		Object valueToCheck = context.getParameterValue(cd.getParameterToCheck());
		Mapped<? extends CohortDefinition> match = cd.getConditionalCohortDefinitions().get(valueToCheck);
		if (match == null) {
			match = cd.getDefaultCohortDefinition();
		}
		if (match != null) {
			Cohort c  = cohortDefinitionService.evaluate(match, context);
			ret = Cohort.union(ret, c);
		}
		return new EvaluatedCohort(ret, cohortDefinition, context);
    }
}
