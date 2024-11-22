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
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an EncounterCohortDefinition and produces a Cohort
 */
@Handler(supports={EncounterCohortDefinition.class})
public class EncounterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public EncounterCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * 
     * @should return all patients with encounters if all arguments to cohort definition are empty
     * @should return correct patients when all non grouping parameters are set
     * @should return correct patients when all parameters are set 
     * @should return correct patients when creation date parameters are set
     * @should return correct patients when time qualifier parameters are set
     * @should return correct patients when provider parameters are set
     * @should not return voided patients
     * @should find patients with encounters on the onOrBefore date if passed in time is at midnight
     * @should find patients with encounters created on the specified date if passed in time is at midnight
     */
    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
    	EncounterCohortDefinition cd = (EncounterCohortDefinition) cohortDefinition;
    	
    	Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingEncounters(
    		cd.getOnOrAfter(), cd.getOnOrBefore(), cd.getTimeQualifier(),
    		cd.getLocationList(), cd.getProviderList(), cd.getEncounterTypeList(), cd.getFormList(),
    		cd.getAtLeastCount(), cd.getAtMostCount(), 
    		cd.getCreatedBy(), cd.getCreatedOnOrAfter(), cd.getCreatedOnOrBefore());
		   	
    	if (cd.isReturnInverse() == Boolean.TRUE) {
    		Cohort baseCohort = context.getBaseCohort();
    		if (baseCohort == null) {
    			baseCohort = Cohorts.allPatients(context);
    		}
    		c = CohortUtil.subtract(baseCohort, c);
    	}
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }
}