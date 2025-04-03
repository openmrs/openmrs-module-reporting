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
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PresenceOrAbsenceCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectCounter;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates an PresenceOrAbsenceCohortDefinition and produces a Cohort
 */
@Handler(supports={PresenceOrAbsenceCohortDefinition.class})
public class PresenceOrAbsenceCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		PresenceOrAbsenceCohortDefinition cd = (PresenceOrAbsenceCohortDefinition) cohortDefinition;

		// Iterate across all of the cohorts to check, and keep track of how many there are for each patient

		ObjectCounter<Integer> counter = new ObjectCounter<Integer>();
		for (Mapped<? extends CohortDefinition> mapped : cd.getCohortsToCheck()) {
			Cohort c = cohortDefinitionService.evaluate(mapped, context);
			for (Integer pId : c.getMemberIds()) {
				counter.increment(pId);
			}
		}

		// Now collect those patients from these cohorts that pass or fail given the min/max allowed

		Cohort patientsToKeep = new Cohort();
		Cohort patientsToRemove = new Cohort();

		Integer min = cd.getPresentInAtLeast();
		Integer max = cd.getPresentInAtMost();

		for (Integer pId : counter.getAllObjectCounts().keySet()) {
			int num = counter.getAllObjectCounts().get(pId);
			boolean keep = (min == null || num >= min) && (max == null || num <= max);
			if (keep) {
				patientsToKeep.addMember(pId);
			}
			else {
				patientsToRemove.addMember(pId);
			}
		}

		// Finally, if zero matches are allowed, we need to include patients from the base cohort that were not in any of the cohorts to check

		if (min == null || min == 0) {
			patientsToKeep = cohortDefinitionService.evaluate(new AllPatientsCohortDefinition(), context);
			patientsToKeep = CohortUtil.subtract(patientsToKeep, patientsToRemove);
		}

		return new EvaluatedCohort(patientsToKeep, cohortDefinition, context);
	}
}