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

import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.Cohort;
import org.openmrs.DrugOrder;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.Match;
import org.openmrs.annotation.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Date;

@Handler(supports = { DrugOrderCohortDefinition.class })
public class DrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	public DrugOrderCohortDefinitionEvaluator() {
	}

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		DrugOrderCohortDefinition drugOrderCohortDefinition = (DrugOrderCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());

		HqlQueryBuilder query = new HqlQueryBuilder();
		query.select("drugOrder.patient.patientId");
		query.from(DrugOrder.class, "drugOrder");

		query.wherePatientIn("drugOrder.patient.patientId", context);
		
		if (drugOrderCohortDefinition.getWhich() == null) drugOrderCohortDefinition.setWhich(Match.ANY); 
			
	    if (drugOrderCohortDefinition.getDrugSets() != null) {
	    	
	    	if (drugOrderCohortDefinition.getWhich() == Match.ANY) {
		    	query.whereInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugSets().toArray());
	    	} 
	    	else if (drugOrderCohortDefinition.getWhich() == Match.ALL) {
		    	query.whereIn("drugOrder.concept", drugOrderCohortDefinition.getDrugSets());
		    	query.groupBy(
			        		"drugOrder.patient.patientId" + " having count(distinct drugOrder.concept.conceptId) = " + drugOrderCohortDefinition.getDrugSets().size());
	    	} 
	    	else if (drugOrderCohortDefinition.getWhich() == Match.NONE) {
		    	query.whereNotInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugSets());
	    	}
	    }
	
	    if (drugOrderCohortDefinition.getDrugConcepts() != null) {
	    	if (drugOrderCohortDefinition.getWhich() == Match.ANY) {
	    		query.whereInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugConcepts().toArray());
	    	} 
	    	else if (drugOrderCohortDefinition.getWhich() == Match.ALL) {
	    		query.whereIn("drugOrder.concept", drugOrderCohortDefinition.getDrugConcepts());
	    		query.groupBy(
	    				"drugOrder.patient.patientId" + " having count(distinct drugOrder.concept.conceptId) = " + drugOrderCohortDefinition.getDrugSets().size());
	    	} 
	    	else if (drugOrderCohortDefinition.getWhich() == Match.NONE) {
	    		query.whereNotInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugConcepts());
	    	}
	    }
	
	    if (drugOrderCohortDefinition.getDrugs() != null) {
	    	if (drugOrderCohortDefinition.getWhich() == Match.ANY) {
	    		query.whereInAny("drugOrder.drug", drugOrderCohortDefinition.getDrugs().toArray());
	    	} 
	    	else if (drugOrderCohortDefinition.getWhich() == Match.ALL) {
	    		query.whereIn("drugOrder.drug", drugOrderCohortDefinition.getDrugs());
	    		query.groupBy(
	    				"drugOrder.patient.patientId" + " having count(distinct drugOrder.drug.drugId) = " + drugOrderCohortDefinition.getDrugs().size());
	    	} 
	    	else if (drugOrderCohortDefinition.getWhich() == Match.NONE) {
	    		query.whereNotInAny("drugOrder.drug", drugOrderCohortDefinition.getDrugs());
	    	}
	    }
	 
    	query.whereLessOrEqualTo("drugOrder.dateActivated", drugOrderCohortDefinition.getActivatedOnOrBefore());
    	query.whereGreaterOrEqualTo("drugOrder.dateActivated", drugOrderCohortDefinition.getActivatedOnOrAfter());
    	query.whereEqual("drugOrder.careSetting", drugOrderCohortDefinition.getCareSetting());
    	
    	if (drugOrderCohortDefinition.getActiveOnOrBefore() != null) {
	    	query.whereNotNull("drugOrder.dateActivated").and()
	    		 .whereLessOrEqualTo("drugOrder.dateStopped", drugOrderCohortDefinition.getActiveOnOrBefore())
	    		 .or()
	    		 .whereLessOrEqualTo("drugOrder.autoExpireDate", drugOrderCohortDefinition.getActiveOnOrBefore());
    	}
    	
    	query.whereNotNull("drugOrder.dateActivated").and()
    		 .whereGreaterEqualOrNull("drugOrder.dateStopped", drugOrderCohortDefinition.getActiveOnOrAfter())
    		 .and()
    		 .whereGreaterEqualOrNull("drugOrder.autoExpireDate", drugOrderCohortDefinition.getActiveOnOrAfter());
    		 
    	query.whereNotNull("drugOrder.dateActivated").and()
    		 .whereLessOrEqualTo("drugOrder.dateActivated", drugOrderCohortDefinition.getActiveOnDate())    		 
    		 .whereGreaterOrNull("drugOrder.dateStopped", drugOrderCohortDefinition.getActiveOnDate())
    		 .and()
    		 .whereGreaterOrNull("drugOrder.autoExpireDate", drugOrderCohortDefinition.getActiveOnDate());
    		 
	    List<Integer> patientIds = evaluationService.evaluateToList(query, Integer.class, context);
	    Cohort cohort = new Cohort(patientIds);
	
	    return new EvaluatedCohort(cohort, drugOrderCohortDefinition, context);
	}
}