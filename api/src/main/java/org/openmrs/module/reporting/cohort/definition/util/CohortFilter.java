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
package org.openmrs.module.reporting.cohort.definition.util;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
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
						ret = Cohort.intersect(ret, c);
					}
				}
			}
		}
		return ret;
	}
}
