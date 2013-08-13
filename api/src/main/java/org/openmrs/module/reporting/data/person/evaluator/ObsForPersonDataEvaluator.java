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
package org.openmrs.module.reporting.data.person.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates an ObsForPersonDataDefinition to produce a PersonData
 */
@Handler(supports=ObsForPersonDataDefinition.class, order=50)
public class ObsForPersonDataEvaluator implements PersonDataEvaluator {

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		ObsForPersonDataDefinition def = (ObsForPersonDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();
		
		hql.append("from 		Obs ");
		hql.append("where 		voided = false ");
		
		if (context.getBaseCohort() != null) {
			hql.append("and 		personId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}
		
		hql.append("and 		concept.conceptId = :question ");
		m.put("question", def.getQuestion().getConceptId());
		
		if (def.getEncounterTypeList() != null && !def.getEncounterTypeList().isEmpty()) {
			List<Integer> ids = new ArrayList<Integer>();
			for (EncounterType encType : def.getEncounterTypeList()) {
				ids.add(encType.getEncounterTypeId());
			}
			hql.append("and		encounter.encounterType.encounterTypeId in (:encounterTypeIds) ");
			m.put("encounterTypeIds", ids);
		}
		
		if (def.getFormList() != null && !def.getFormList().isEmpty()) {
			List<Integer> ids = new ArrayList<Integer>();
			for (Form encForm : def.getFormList()) {
				ids.add(encForm.getFormId());
			}
			hql.append("and		encounter.form.formId in (:formIds) ");
			m.put("formIds", ids);
		}
		
		if (def.getOnOrAfter() != null) {
			hql.append("and		obsDatetime >= :onOrAfter ");
			m.put("onOrAfter", def.getOnOrAfter());
		}
		
		if (def.getOnOrBefore() != null) {
			hql.append("and		obsDatetime <= :onOrBefore ");
			m.put("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getOnOrBefore()));
		}
		
		hql.append("order by 	obsDatetime " + (def.getWhich() == TimeQualifier.LAST ? "desc" : "asc"));
		
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		
		ListMap<Integer, Obs> obsForPatients = new ListMap<Integer, Obs>();
		for (Object o : queryResult) {
			Obs obs = (Obs)o;
			obsForPatients.putInList(obs.getPersonId(), obs);
		}
		
		for (Integer pId : obsForPatients.keySet()) {
			List<Obs> l = obsForPatients.get(pId);
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
