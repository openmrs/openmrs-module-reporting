/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.util;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Utility method that which allows you to evaluate multiple
 * CohortDefinitions and returning the resulting Cohort which
 * represents the intersection of all evaluated Cohorts
 */
public class CohortFilter {
	
	/**
	 * Returns the intersection of all passed CohortDefinitions
	 * @param context - The EvaluationContext to utilize
	 * @param definitions - The CohortDefinitions to evaluate
	 * @return - The intersection of the Cohorts produced by each evaluated CohortDefinitions
	 * @throws EvaluationException if any of the passed definitions could not be evaluated
	 */
	public static Cohort filter(EvaluationContext context, Mapped<? extends CohortDefinition>... definitions) throws EvaluationException {
		Cohort ret = context.getBaseCohort();
		if (definitions != null) {
			for (Mapped<? extends CohortDefinition> d : definitions) {
				if (d != null) {
					Cohort c = Context.getService(CohortDefinitionService.class).evaluate(d, context);
					if (ret == null) {
						ret = c;
					}
					else {
						ret = CohortUtil.intersect(ret, c);
					}
				}
			}
		}
		return ret;
	}
}
