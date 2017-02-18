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
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.List;

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
    		getLocationList(cd), cd.getProviderList(), cd.getEncounterTypeList(), cd.getFormList(),
    		cd.getAtLeastCount(), cd.getAtMostCount(), 
    		cd.getCreatedBy(), cd.getCreatedOnOrAfter(), cd.getCreatedOnOrBefore());
		   	
    	if (cd.isReturnInverse() == Boolean.TRUE) {
    		Cohort baseCohort = context.getBaseCohort();
    		if (baseCohort == null) {
    			baseCohort = Cohorts.allPatients(context);
    		}
    		c = Cohort.subtract(baseCohort, c);
    	}
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }

    private List<Location> getLocationList(EncounterCohortDefinition cd) {
    	if (cd.isIncludeChildLocations()) {
    		return DefinitionUtil.getAllLocationsAndChildLocations(cd.getLocationList());
		}
		return cd.getLocationList();
	}
}