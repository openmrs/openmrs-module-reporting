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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an PersonAttributeCohortDefinition and produces a Cohort
 */
@Handler(supports={PersonAttributeCohortDefinition.class})
public class PersonAttributeCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public PersonAttributeCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	
    	PersonAttributeCohortDefinition pacd = (PersonAttributeCohortDefinition) cohortDefinition;
		List<String> values = new ArrayList<String>();
		
		if (pacd.getValues() != null) {
			values.addAll(pacd.getValues());
		}
		if (pacd.getValueConcepts() != null) {
			for (Concept c : pacd.getValueConcepts()) {
				values.add(c.serialize());
			}
		}
		if (pacd.getValueLocations() != null) {
			for (Location l : getValueLocations(pacd)) {
				values.add(l.serialize());
			}
		}
    	
		CohortQueryService cqs = Context.getService(CohortQueryService.class);
    	Cohort c = cqs.getPatientsHavingPersonAttributes(pacd.getAttributeType(), values);
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }

    private List<Location> getValueLocations(PersonAttributeCohortDefinition pacd) {
    	if (pacd.isIncludeChildLocations()) {
    		return DefinitionUtil.getAllLocationsAndChildLocations(pacd.getValueLocations());
		}
		return pacd.getValueLocations();
	}
}