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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DrugOrderSet;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.stereotype.Component;

/**
 * Evaluates an DrugOrdersForPatientDataDefinition to produce a PatientData
 */
@Component
@OpenmrsProfile(openmrsVersion = "1.10")
@Handler(supports = DrugOrdersForPatientDataDefinition.class, order = 49)
public class DrugOrdersForPatientDataEvaluator1_10 extends DrugOrdersForPatientDataEvaluator {
	
	/**
	 * @see org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return drug orders restricted by drug
	 * @should return drug orders restricted by drug concept
	 * @should return drug orders restricted by drug concept set
	 * @should return drug orders active on a particular date
	 * @should return drug orders started on or before a given date
	 * @should return drug orders started on or after a given date
	 * @should return drug orders completed on or before a given date
	 * @should return drug orders completed on or after a given date
	 */
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
	    throws EvaluationException {
		
		DrugOrdersForPatientDataDefinition def = (DrugOrdersForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from 		DrugOrder ");
		hql.append("where 		voided = false ");
		hql.append("and 		action != 'DISCONTINUED' ");
		
		if (context.getBaseCohort() != null) {
			hql.append("and 	patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		List<Integer> drugIds = new ArrayList<Integer>();
		List<Integer> conceptIds = new ArrayList<Integer>();
		
		if (def.getDrugsToInclude() != null) {
			for (Drug d : def.getDrugsToInclude()) {
				drugIds.add(d.getDrugId());
			}
		}
		if (def.getDrugConceptsToInclude() != null) {
			for (Concept concept : def.getDrugConceptsToInclude()) {
				conceptIds.add(concept.getConceptId());
			}
		}
		if (def.getDrugConceptSetsToInclude() != null) {
			for (Concept concept : def.getDrugConceptSetsToInclude()) {
				if (concept.isSet()) {
					for (ConceptSet setMember : concept.getConceptSets()) {
						conceptIds.add(setMember.getConcept().getConceptId());
					}
				}
			}
		}
		
		if (drugIds.size() > 0 && conceptIds.size() > 0) {
			hql.append("and 	( drug.drugId in (:drugIds) or concept.conceptId in (:conceptIds) ) ");
			m.put("drugIds", drugIds);
			m.put("conceptIds", conceptIds);
		} else if (drugIds.size() > 0) {
			hql.append("and 	drug.drugId in (:drugIds) ");
			m.put("drugIds", drugIds);
		} else if (conceptIds.size() > 0) {
			hql.append("and 	concept.conceptId in (:conceptIds) ");
			m.put("conceptIds", conceptIds);
		}
		
		if (def.getActiveOnDate() != null) {
			hql.append("and		startDate <= :activeOnDate ");
			hql.append("and		(autoExpireDate is null or autoExpireDate > :activeOnDate) ");
			hql.append("and		(dateStopped is null or dateStopped > :activeOnDate) ");
			m.put("activeOnDate", DateUtil.getEndOfDayIfTimeExcluded(def.getActiveOnDate()));
		}
		
		if (def.getStartedOnOrBefore() != null) {
			hql.append("and		startDate <= :startedOnOrBefore ");
			m.put("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getStartedOnOrBefore()));
		}
		
		if (def.getStartedOnOrAfter() != null) {
			hql.append("and		startDate >= :startedOnOrAfter ");
			m.put("startedOnOrAfter", def.getStartedOnOrAfter());
		}
		
		if (def.getCompletedOnOrBefore() != null) {
			hql.append("and		( (autoExpireDate is not null and autoExpireDate <= :completedOnOrBefore) or ");
			hql.append("		  (dateStopped is not null and dateStopped <= :completedOnOrBefore) ) ");
			m.put("completedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getCompletedOnOrBefore()));
		}
		
		if (def.getCompletedOnOrAfter() != null) {
			hql.append("and		( (autoExpireDate is not null and autoExpireDate >= :completedOnOrAfter) or ");
			hql.append("		  (dateStopped is not null and dateStopped >= :completedOnOrAfter) ) ");
			m.put("completedOnOrAfter", DateUtil.getEndOfDayIfTimeExcluded(def.getCompletedOnOrAfter()));
		}
		
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		for (Object o : queryResult) {
			DrugOrder drugOrder = (DrugOrder) o;
			Integer pId = drugOrder.getPatient().getPatientId(); // TODO: Make this more efficient via HQL
			DrugOrderSet h = (DrugOrderSet) c.getData().get(pId);
			if (h == null) {
				h = new DrugOrderSet();
				c.addData(pId, h);
			}
			h.add(drugOrder);
		}
		
		return c;
	}
}
