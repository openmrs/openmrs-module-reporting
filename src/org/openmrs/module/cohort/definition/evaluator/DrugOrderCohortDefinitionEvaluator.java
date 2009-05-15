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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * This interfaces provides the functionality to evaluate a CohortDefinition and return a Cohort.
 */
@Handler(supports={DrugOrderCohortDefinition.class})
public class DrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public DrugOrderCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	DrugOrderCohortDefinition doc = (DrugOrderCohortDefinition) cohortDefinition;
		
		List<Integer> drugIds = new ArrayList<Integer>();
		for (Drug d : doc.getDrugListToUse(context)) {
			drugIds.add(d.getDrugId());
		}
		
		Date fromDate = doc.getCalculatedFromDate(context);
		Date toDate = doc.getCalculatedToDate(context);
		
		PatientSetService pss = Context.getPatientSetService();
		return pss.getPatientsHavingDrugOrder(null, drugIds, doc.getAnyOrAll(), fromDate, toDate);
    }
}