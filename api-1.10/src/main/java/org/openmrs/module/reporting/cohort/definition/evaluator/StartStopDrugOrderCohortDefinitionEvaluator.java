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
import org.openmrs.Order;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.StartStopDrugOrderCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.Match;
import org.openmrs.annotation.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Date;

@Handler(supports = { StartStopDrugOrderCohortDefinition.class })
public class StartStopDrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	public StartStopDrugOrderCohortDefinitionEvaluator() {
	}

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		StartStopDrugOrderCohortDefinition startStopDrugOrderDef = (StartStopDrugOrderCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());

		HqlQueryBuilder query = new HqlQueryBuilder();
		query.select("drugOrder.patient.patientId");
		query.from(DrugOrder.class, "drugOrder");

		query.wherePatientIn("drugOrder.patient.patientId", context);
		
		if (startStopDrugOrderDef.getState() == null) {
			startStopDrugOrderDef.setState(Match.STARTED);
		} 
		
		if (startStopDrugOrderDef.getState() == Match.STARTED) {
			
			query.whereEqual("drugOrder.action", Order.Action.NEW) 
			.whereNull("drugOrder.previousOrder");
    	} 
    	else if (startStopDrugOrderDef.getState() == Match.STOPPED) {
    		
    		query.whereEqual("drugOrder.action", Order.Action.DISCONTINUE)
    		.whereGreaterOrEqualTo("drugOrder.dateActivated", startStopDrugOrderDef.getOnOrAfter())
    		.whereLessOrEqualTo("drugOrder.dateActivated", startStopDrugOrderDef.getOnOrBefore())
    		.or()
    		.whereGreaterOrEqualTo("COALESCE(drugOrder.dateStopped, drugOrder.autoExpireDate)", startStopDrugOrderDef.getOnOrAfter())
    		.whereLessOrEqualTo("COALESCE(drugOrder.dateStopped, drugOrder.autoExpireDate)", startStopDrugOrderDef.getOnOrBefore())
    		.where("drugOrder.orderId NOT IN(SELECT drugOrder.previousOrder FROM drugOrder WHERE drugOrder.previousOrder IS NOT NULL)");
    	} 
    	else if (startStopDrugOrderDef.getState() == Match.CHANGED) {
    	 
    		query.whereNotNull("drugOrder.dateActivated")
   		 	.whereGreaterOrEqualTo("COALESCE(drugOrder.dateStopped, drugOrder.autoExpireDate)", startStopDrugOrderDef.getOnOrAfter())
   		 	.whereLessOrEqualTo("COALESCE(drugOrder.dateStopped, drugOrder.autoExpireDate)", startStopDrugOrderDef.getOnOrBefore());
    	}
			
	    if (startStopDrugOrderDef.getDrugSets() != null) {
	    	query.whereIn("drugOrder.concept", startStopDrugOrderDef.getDrugSets());
	    }
	
	    if (startStopDrugOrderDef.getDrugConcepts() != null) {
	    	query.whereIn("drugOrder.concept", startStopDrugOrderDef.getDrugConcepts());
	    }
	
	    if (startStopDrugOrderDef.getDrugs() != null) {
	    	query.whereIn("drugOrder.drug", startStopDrugOrderDef.getDrugs());
	    }
	 
	    List<Integer> patientIds = evaluationService.evaluateToList(query, Integer.class, context);
	    Cohort cohort = new Cohort(patientIds);
	
	    return new EvaluatedCohort(cohort, startStopDrugOrderDef, context);
	}
}