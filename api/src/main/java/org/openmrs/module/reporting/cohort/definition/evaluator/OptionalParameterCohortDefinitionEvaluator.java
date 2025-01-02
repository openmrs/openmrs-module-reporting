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
