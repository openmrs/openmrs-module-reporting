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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an EncountersForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=EncountersForPatientDataDefinition.class, order=50)
public class EncountersForPatientDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		EncountersForPatientDataDefinition def = (EncountersForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from 		Encounter ");
		hql.append("where 		voided = false ");
		
		if (context.getBaseCohort() != null) {
			hql.append("and 		patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		if (def.getTypes() != null && !def.getTypes().isEmpty()) {
			List<Integer> ids = new ArrayList<Integer>();
			for (EncounterType encType : def.getTypes()) {
				ids.add(encType.getEncounterTypeId());
			}
			hql.append("and		encounterType.encounterTypeId in (:ids) ");
			m.put("ids", ids);
		}

        if (def.getOnlyInActiveVisit()) {
            hql.append("and		visit.stopDatetime is null ");
        }

		if (def.getOnOrAfter() != null) {
			hql.append("and		encounterDatetime >= :onOrAfter ");
			m.put("onOrAfter", def.getOnOrAfter());
		}

		if (def.getOnOrBefore() != null) {
			hql.append("and		encounterDatetime <= :onOrBefore ");
			m.put("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getOnOrBefore()));
		}
		
		hql.append("order by 	encounterDatetime " + (def.getWhich() == TimeQualifier.LAST ? "desc" : "asc"));
		
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		
		ListMap<Integer, Encounter> encsForPatients = new ListMap<Integer, Encounter>();
		for (Object o : queryResult) {
			Encounter e = (Encounter)o;
			encsForPatients.putInList(e.getPatientId(), e);
		}
		
		for (Integer pId : encsForPatients.keySet()) {
			List<Encounter> l = encsForPatients.get(pId);
			if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
				c.addData(pId, l.get(0));
			}
			else {
				c.addData(pId, l);
			}
		}
		
		return c;
	}
}
