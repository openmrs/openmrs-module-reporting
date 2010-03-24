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

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.toreview.DrugOrderStopCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * This interfaces provides the functionality to evaluate a CohortDefinition and return a Cohort.
 */
@Handler(supports={DrugOrderStopCohortDefinition.class})
public class DrugOrderStopCohortDefinitionEvaluator  implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public DrugOrderStopCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	DrugOrderStopCohortDefinition doc = (DrugOrderStopCohortDefinition) cohortDefinition;
    	
    	// TODO: Deprecate PSS methods and move logic into here
		PatientSetService pss = Context.getPatientSetService();

    	Date fromDate = doc.getCalculatedFromDate(context);
    	Date toDate = doc.getCalculatedToDate(context);
		
		return pss.getPatientsHavingDrugOrder(doc.getDrugList(), doc.getGenericDrugList(), null, null, 
											  fromDate, toDate, doc.getDiscontinued(), doc.getDiscontinuedReasonList());
    }
}