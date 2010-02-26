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
package org.openmrs.module.cohort.definition.evaluator;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * Evaluates an PatientCharacteristicCohortDefinition and produces a Cohort
 */
@Handler(supports={GenderCohortDefinition.class})
public class GenderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public GenderCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * 
     * @should return non voided patients
     * @should return male patients when gender equals male
     * @should return female patients when gender equals female 
     * @should return patients with no gender when gender equals unknown 
     * @should return all patients when gender equals empty string
     * @should return all patients when gender is null
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	GenderCohortDefinition gcd = (GenderCohortDefinition) cohortDefinition;

		PatientSetService pss = Context.getPatientSetService();
		return pss.getPatientsByCharacteristics(gcd.getGender(), null, null, null, null, null, null, null);
    }
}