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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a PreferredNameDataDefinition to produce a PersonData
 */
@Handler(supports=PreferredNameDataDefinition.class, order=50)
public class PreferredNameDataEvaluator implements PersonDataEvaluator {

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return the preferred name for all persons
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		Map<String, Object> m = new HashMap<String, Object>();
		
		String hql = "from PersonName where voided = false ";
		if (context.getBaseCohort() != null) {
			hql += "and person.personId in (:personIds) ";
			m.put("personIds", context.getBaseCohort());
		}
		hql += "order by preferred asc";
		
		List<Object> queryResult = qs.executeHqlQuery(hql, m);
		for (Object o : queryResult) {
			PersonName pn = (PersonName)o;
			c.addData(pn.getPerson().getPersonId(), pn);  // TODO: This is probably inefficient.  Try to improve this
		}
		return c;
	}
}
