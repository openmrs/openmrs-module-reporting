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
import org.openmrs.module.reporting.cohort.CohortUtil;
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
			ret = CohortUtil.union(ret, c);
		}
		return new EvaluatedCohort(ret, cohortDefinition, context);
    }
}
