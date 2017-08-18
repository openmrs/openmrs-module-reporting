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
package org.openmrs.module.reporting.data.visit.evaluator;

import java.util.List;

import org.openmrs.Order;
import org.openmrs.annotation.Handler;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.OrderForVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates an ObsForVisitDataDefinition to produce a VisitData that contains the observations recorded for a visit, based on a provided concept
 */
@Handler(supports=OrderForVisitDataDefinition.class, order=50)
public class OrderForVisitDataEvaluator implements VisitDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Autowired
	PatientDataService patientDataService;

	@Autowired
	OrderService orderService;

	@Autowired
	EncounterService encounterService;

	@Autowired
	PatientService patientService;
	
	@Autowired
	VisitDataService visitDataService;

	/** 
	 * @see VisitDataEvaluator#evaluate(VisitDataDefinition, EvaluationContext)
	 * @should return the orders that matches the passed definition configuration
	 */
	public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {

		OrderForVisitDataDefinition visitDef = (OrderForVisitDataDefinition) definition;

		EvaluatedVisitData evaluatedData = new EvaluatedVisitData(visitDef, context);
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return evaluatedData;
		}
		HqlQueryBuilder q = new HqlQueryBuilder();

		q.select("v.visitId", "o");
		q.from(Order.class, "o");
		q.whereIn("o.orderType", visitDef.getTypes());
		q.innerJoin("o.encounter", "e");
		q.innerJoin("e.visit", "v");
		q.whereVisitIn("v.visitId", context);
		
		if (ModuleUtil.compareVersion(OpenmrsConstants.OPENMRS_VERSION, "1.10") < 0) {
            q.orderAsc("o.startDate");
        }
		else {
		    q.orderAsc("o.dateActivated");
        }
		
		List<Object[]> queryResult = evaluationService.evaluateToList(q, context);
		
		ListMap<Integer, Order> ordersForVisits = new ListMap<Integer, Order>();
		for (Object[] row : queryResult) {
			ordersForVisits.putInList((Integer)row[0], (Order)row[1]);
		}
		
		for (Integer vid : ordersForVisits.keySet()) {
			List<Order> l = ordersForVisits.get(vid);
			evaluatedData.addData(vid, l);
		}
		return evaluatedData;
	}

}