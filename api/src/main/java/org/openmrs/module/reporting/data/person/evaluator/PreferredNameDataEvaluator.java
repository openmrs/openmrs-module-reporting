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

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a PreferredNameDataDefinition to produce a PersonData
 */
@Handler(supports = PreferredNameDataDefinition.class, order = 50)
public class PreferredNameDataEvaluator implements PersonDataEvaluator {

	/**
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 *
	 * @should return the most preferred name for each person in the passed context
	 * @should return empty result set for an empty base cohort
	 * @should return the preferred name for all persons
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		if (context != null && context.getBaseCohort() != null && !context.getBaseCohort().isEmpty()) {
			DataSetQueryService qs = Context.getService(DataSetQueryService.class);

			// orders all person names so preferred comes last for each person id
			String hql = "select pn.person.personId, pn" +
					" from PersonName as pn" +
					" where voided = false " +
					" and pn.person.personId in (:personIds)" +
					" order by preferred asc";

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("personIds", context.getBaseCohort());

			// overwrite person name for each person, finishing with the preferred if it exists
			List<Object> queryResult = qs.executeHqlQuery(hql, m);
			for (Object o : queryResult) {
				Object[] parts = (Object[]) o;
				if (parts.length == 2) {
					Integer pId = (Integer) parts[0];
					PersonName pn = (PersonName) parts[1];
					c.addData(pId, pn);
				}
			}
		}
		return c;
	}
}
