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
package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DrugOrderSet;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates an DrugOrdersForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=DrugOrdersForPatientDataDefinition.class, order=50)
public class DrugOrdersForPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return drug orders restricted by drug
	 * @should return drug orders restricted by drug concept
	 * @should return drug orders restricted by drug concept set
	 * @should return drug orders active on a particular date
	 * @should return drug orders started on or before a given date
	 * @should return drug orders started on or after a given date
	 * @should return drug orders completed on or before a given date
	 * @should return drug orders completed on or after a given date
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		DrugOrdersForPatientDataDefinition def = (DrugOrdersForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}
		
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("do.patient.patientId", "do");
		q.from(DrugOrder.class, "do");
		q.whereIdIn("do.patient.patientId", context.getBaseCohort());

		List<Concept> concepts = null;

		if (def.getDrugConceptsToInclude() != null) {
			concepts = def.getDrugConceptsToInclude();
		}
		if (def.getDrugConceptSetsToInclude() != null) {
			if (concepts == null) {
				concepts = new ArrayList<Concept>();
			}
			for (Concept conceptSet : def.getDrugConceptSetsToInclude()) {
				if (conceptSet.isSet()) {
					concepts.addAll(conceptSet.getSetMembers());
				}
			}
		}

		if (def.getDrugsToInclude() != null && concepts != null) {
			q.startGroup();
			q.whereIn("drug", def.getDrugsToInclude());
			q.or();
			q.whereIn("concept", concepts);
			q.endGroup();
		}
		else if (def.getDrugsToInclude() != null) {
			q.whereIn("drug", def.getDrugsToInclude());
		}
		else if (concepts != null) {
			q.whereIn("concept", concepts);
		}
		
		if (def.getActiveOnDate() != null) {
			q.whereLessOrEqualTo("startDate", def.getActiveOnDate());
			q.whereGreaterOrNull("autoExpireDate", def.getActiveOnDate());
			q.whereGreaterOrNull("discontinuedDate", def.getActiveOnDate());
		}
		
		if (def.getStartedOnOrBefore() != null) {
			q.whereLessOrEqualTo("startDate", def.getStartedOnOrBefore());
		}
		
		if (def.getStartedOnOrAfter() != null) {
			q.whereGreaterOrEqualTo("startDate", def.getStartedOnOrAfter());
		}
		
		if (def.getCompletedOnOrBefore() != null) {
			q.startGroup();
			q.whereLessOrEqualTo("autoExpireDate", def.getCompletedOnOrBefore());
			q.or();
			q.whereLessOrEqualTo("discontinuedDate", def.getCompletedOnOrBefore());
			q.endGroup();
		}

		if (def.getCompletedOnOrAfter() != null) {
			q.startGroup();
			q.whereGreaterOrEqualTo("autoExpireDate", def.getCompletedOnOrAfter());
			q.or();
			q.whereGreaterOrEqualTo("discontinuedDate", def.getCompletedOnOrAfter());
			q.endGroup();
		}
		
		List<Object[]> results = evaluationService.evaluateToList(q);
		for (Object[] row : results) {
			Integer pId = (Integer)row[0];
			DrugOrder drugOrder = (DrugOrder)row[1];
			DrugOrderSet drugOrderSet = (DrugOrderSet)c.getData().get(pId);
			if (drugOrderSet == null) {
				drugOrderSet = new DrugOrderSet();
				c.addData(pId, drugOrderSet);
			}
			drugOrderSet.add(drugOrder);
		}
		
		return c;
	}
}
