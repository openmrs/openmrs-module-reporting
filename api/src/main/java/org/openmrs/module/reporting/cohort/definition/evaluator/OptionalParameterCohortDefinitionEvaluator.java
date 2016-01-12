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
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.OptionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = OptionalParameterCohortDefinition.class)
public class OptionalParameterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		OptionalParameterCohortDefinition cd = (OptionalParameterCohortDefinition) cohortDefinition;
		boolean hasParameters = true;
		for (String paramName : cd.getParametersToCheck()) {
			if (context.getParameterValue(paramName) == null) {
				hasParameters = false;
			}
		}
		Cohort ret;
		if (hasParameters) {
			ret = cohortDefinitionService.evaluate(cd.getWrappedCohortDefinition(), context);
		}
		else {
			ret = cohortDefinitionService.evaluate(new AllPatientsCohortDefinition(), context);
		}
		return new EvaluatedCohort(ret, cd, context);
    }
}
