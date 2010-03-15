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
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

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
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * 
     * @should return all patients with encounters if all arguments to cohort definition are empty 
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	EncounterCohortDefinition ed = (EncounterCohortDefinition) cohortDefinition;
		
		PatientSetService pss = Context.getPatientSetService();

    	Date fromDate = ed.getCalculatedFromDate(context);
    	Date toDate = ed.getCalculatedToDate(context);
    	
    	// there was a bug in core before rev 12432 where passing an empty list for the first parameter would throw an exception
    	List<EncounterType> encTypeList = ed.getEncounterTypeList();
    	if (encTypeList != null && encTypeList.size() == 0)
    		encTypeList = null;
    	
    	Cohort c = pss.getPatientsHavingEncounters(encTypeList, ed.getLocation(), 
    										   ed.getForm(), fromDate, toDate, 
    										   ed.getAtLeastCount(), ed.getAtMostCount());
    	
    	if (ed.isReturnInverse() == Boolean.TRUE) {
    		Cohort baseCohort = context.getBaseCohort();
    		if (baseCohort == null) {
    			baseCohort = Context.getPatientSetService().getAllPatients();
    		}
    		return Cohort.subtract(baseCohort, c);
    	}
    	return c;
    }
}