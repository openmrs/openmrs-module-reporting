/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.person.PersonDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.AllPersonQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The logic that evaluates a {@link AllPersonQuery} and produces an {@link PersonQueryResult}
 */
@Handler(supports=AllPersonQuery.class)
public class AllPersonQueryEvaluator implements PersonQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * Public constructor
	 */
	public AllPersonQueryEvaluator() { }
	
	/**
	 * @see PersonQueryEvaluator#evaluate(PersonQuery, EvaluationContext)
	 * @should return all of the person ids for all patients in the defined query
	 * @should filter results by patient and person given an PersonEvaluationContext
	 * @should filter results by patient given an EvaluationContext
	 */
	public PersonQueryResult evaluate(PersonQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		AllPersonQuery query = (AllPersonQuery) definition;
		PersonQueryResult result = new PersonQueryResult(query, context);
		result.setMemberIds(PersonDataUtil.getPersonIdsForContext(context, true));
		return result;
	}
}
