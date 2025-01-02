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
			for (Location l : pacd.getValueLocations()) {
				values.add(l.serialize());
			}
		}
    	
		CohortQueryService cqs = Context.getService(CohortQueryService.class);
    	Cohort c = cqs.getPatientsHavingPersonAttributes(pacd.getAttributeType(), values);
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }
}