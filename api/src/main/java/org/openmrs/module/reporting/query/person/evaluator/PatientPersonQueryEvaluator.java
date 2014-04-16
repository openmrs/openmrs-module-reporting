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
package org.openmrs.module.reporting.query.person.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PatientPersonQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The logic that evaluates a {@link PatientPersonQuery} and produces an {@link PatientPersonQuery}
 */
@Handler(supports=PatientPersonQuery.class)
public class PatientPersonQueryEvaluator implements PersonQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	CohortDefinitionService cohortDefinitionService;
	
	/**
	 * Public constructor
	 */
	public PatientPersonQueryEvaluator() { }
	
	/**
	 * @see PatientPersonQueryEvaluator#evaluate(PersonQuery, EvaluationContext)
	 * @should return all of the person ids for all patients in the defined patient query
	 */
	public PersonQueryResult evaluate(PersonQuery definition, EvaluationContext context) throws EvaluationException {
		PatientPersonQuery query = (PatientPersonQuery) definition;
		EvaluatedCohort c = cohortDefinitionService.evaluate(query.getPatientQuery(), context);
		PersonQueryResult r = new PersonQueryResult(query, context);
		r.setMemberIds(c.getMemberIds());
		return r;
	}
}
